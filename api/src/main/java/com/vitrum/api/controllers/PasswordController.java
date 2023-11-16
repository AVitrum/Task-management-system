package com.vitrum.api.controllers;

import com.vitrum.api.services.PasswordService;
import com.vitrum.api.dto.Request.ChangePasswordRequest;
import com.vitrum.api.dto.Request.ResetPasswordRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService service;

    @GetMapping("/password/recoverycode/{email}")
    public ResponseEntity<?> getRecoverycode(
            @PathVariable String email
    ) {
        try {
            service.getRecoverycode(email);
            return ResponseEntity.ok().body("Sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/password")
    public ResponseEntity<?> change(
            @Valid @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        try {
            service.changePassword(request, connectedUser);
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/password/reset")
    public ResponseEntity<?> reset(
            @RequestBody ResetPasswordRequest request
    ) {
        try {
            service.resetPassword(request);
            return ResponseEntity.ok().body("Changed");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
