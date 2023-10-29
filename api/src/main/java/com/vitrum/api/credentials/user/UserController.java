package com.vitrum.api.credentials.user;

import com.vitrum.api.dto.Request.ChangeUserRoleRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PutMapping("/changeRole")
    public ResponseEntity<?> changeRole(
            @RequestBody ChangeUserRoleRequest request
    ) {
        try {
            service.changeRole(request);
            return ResponseEntity.ok("Changed");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/ban")
    public ResponseEntity<?> ban(
        @RequestBody Map<String, String> username
    ) {
        try {
            service.ban(username.get("username"));
            return ResponseEntity.ok("Okay");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
