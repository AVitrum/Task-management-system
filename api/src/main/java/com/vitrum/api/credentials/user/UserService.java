package com.vitrum.api.credentials.user;

import com.vitrum.api.dto.Request.ChangeUserRoleRequest;
import com.vitrum.api.config.JwtService;
import com.vitrum.api.dto.Response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final JwtService jwtService;

    public UserProfileResponse profile(HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        String userEmail = jwtService.extractEmail(jwt);
        User user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getTrueUsername())
                .role(user.getRole())
                .build();
    }

    public void changeRole(ChangeUserRoleRequest request) {
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        try {
            user.setRole(Role.valueOf(request.getRole()));
            repository.save(user);
        } catch (IllegalArgumentException e) {
            user.setRole(Role.USER);
            repository.save(user);
            throw new IllegalArgumentException("Wrong role");
        }
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header!");
    }
}
