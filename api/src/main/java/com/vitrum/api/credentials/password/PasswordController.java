package com.vitrum.api.credentials.password;

import com.vitrum.api.dto.Request.ChangePasswordRequest;
import com.vitrum.api.dto.Request.GenerateRecoverycodeRequest;
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

    @PatchMapping("/password")
    public ResponseEntity<?> changePassword(
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

    @GetMapping("/password/recoverycode")
    public ResponseEntity<?> getRecoverycode(
            @RequestBody GenerateRecoverycodeRequest request
    ) {
        try {
            service.getRecoverycode(request.getEmail());
            return ResponseEntity.ok().body("Sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/password/reset")
    public ResponseEntity<?> resetPassword(
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
