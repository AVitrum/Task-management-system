package com.vitrum.api.controller;

import com.vitrum.api.dto.Request.AuthenticationRequest;
import com.vitrum.api.dto.Response.AuthenticationResponse;
import com.vitrum.api.dto.Request.RegisterRequest;
import com.vitrum.api.dto.Response.UserProfileResponse;
import com.vitrum.api.entity.Role;
import com.vitrum.api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin("http://16.170.214.192:5173")
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile() { //(HttpServletRequest request) {
//        return ResponseEntity.ok(service.getUserProfile(request));
        return ResponseEntity.ok(UserProfileResponse.builder().username("Test").email("test@g").role(Role.USER).id(0L).build());
    }
}

