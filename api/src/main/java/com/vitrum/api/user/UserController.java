package com.vitrum.api.user;

import com.vitrum.api.dto.Request.ChangePasswordRequest;
import com.vitrum.api.dto.Request.PasswordRecoveryRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping("/profile")
    public ResponseEntity<?> profile(
            HttpServletRequest request
    ) {
        try {
            return ResponseEntity.ok(service.profile(request));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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

    @GetMapping("/recoverycode")
    public ResponseEntity<?> getRecoverycode(
            @RequestBody PasswordRecoveryRequest passwordRecoveryRequest
    ) {
        try {
            service.getRecoverycode(passwordRecoveryRequest.getEmail());
            return ResponseEntity.ok().body("Sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
