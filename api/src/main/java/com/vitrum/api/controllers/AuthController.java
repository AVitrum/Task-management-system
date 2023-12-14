package com.vitrum.api.controllers;

import com.vitrum.api.services.interfaces.AuthenticationService;
import com.vitrum.api.services.implementations.LogoutServiceImpl;
import com.vitrum.api.data.request.AuthenticationRequest;
import com.vitrum.api.data.request.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService service;
    private final LogoutServiceImpl logoutServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        service.refreshToken(request, response);
    }

    @DeleteMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        logoutServiceImpl.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    }
}