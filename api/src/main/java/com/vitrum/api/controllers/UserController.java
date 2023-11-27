package com.vitrum.api.controllers;

import com.vitrum.api.data.request.ChangeUserCredentials;
import com.vitrum.api.data.request.RegisterRequest;
import com.vitrum.api.services.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody RegisterRequest request) {
        try {
            service.create(request);
            return ResponseEntity.ok("Created");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(HttpServletRequest request) {
        try {
            return ResponseEntity.ok(service.profile(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/credentials")
    public ResponseEntity<?> changeCredentials(@RequestBody ChangeUserCredentials request) {
        try {
            service.changeCredentials(request);
            return ResponseEntity.ok("Changed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/status")
    public ResponseEntity<?> changeStatus(@RequestBody Map<String, String> username) {
        try {
            service.changeStatus(username.get("username"));
            return ResponseEntity.ok("Okay");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
