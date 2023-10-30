package com.vitrum.api.credentials.user;

import com.vitrum.api.credentials.authentication.AuthService;
import com.vitrum.api.dto.Request.ChangeUserCredentials;
import com.vitrum.api.config.JwtService;
import com.vitrum.api.dto.Request.RegisterRequest;
import com.vitrum.api.dto.Response.UserProfileResponse;
import com.vitrum.api.util.Converter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final AuthService authService;
    private final Converter converter;
    private final JwtService jwtService;

    public UserProfileResponse profile(HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        String userEmail = jwtService.extractEmail(jwt);
        User user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return converter.mapUserToUserProfileResponse(user);
    }

    public void create(RegisterRequest request) {
        authService.register(request);
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        try {
            user.setRole(Role.valueOf(request.getRole()));
            repository.save(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error");
        }
    }

    public void changeCredentials(ChangeUserCredentials request) {
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        try {
            if (request.getRole() != null) {
                user.setRole(Role.valueOf(request.getRole()));
            }
            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
            if (request.getNewUsername() != null) {
                user.setUsername(request.getNewUsername());
            }
            repository.save(user);
        } catch (IllegalArgumentException e) {
            user.setRole(Role.USER);
            repository.save(user);
            throw new IllegalArgumentException("Wrong Credentials");
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("User with the same email/username already exists.");
        }
    }

    public void ban(String username) {
        var user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setIsBanned(true);
        repository.save(user);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header!");
    }
}
