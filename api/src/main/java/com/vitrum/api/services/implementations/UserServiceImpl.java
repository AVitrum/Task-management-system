package com.vitrum.api.services.implementations;

import com.vitrum.api.data.enums.Role;
import com.vitrum.api.data.models.User;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.config.JwtService;
import com.vitrum.api.data.request.ChangeUserCredentials;
import com.vitrum.api.data.request.RegisterRequest;
import com.vitrum.api.data.response.UserProfileResponse;
import com.vitrum.api.services.interfaces.UserService;
import com.vitrum.api.util.Converter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final AuthenticationServiceImpl authenticationServiceImpl;
    private final Converter converter;
    private final JwtService jwtService;

    @Override
    public UserProfileResponse profile(HttpServletRequest request) {
        String jwt = extractJwtFromRequest(request);
        String userEmail = jwtService.extractEmail(jwt);
        User user = repository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return converter.mapUserToUserProfileResponse(user);
    }

    @Override
    public void create(RegisterRequest request) {
        authenticationServiceImpl.register(request);
        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        try {
            user.setRole(Role.valueOf(request.getRole()));
            repository.save(user);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error");
        }
    }

    @Override
    public void changeCredentials(ChangeUserCredentials request) {
        User user = repository.findByUsername(request.getUsername())
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

    @Override
    public void changeStatus(String username) {
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setIsBanned(!user.getIsBanned());
        repository.save(user);

        authenticationServiceImpl.revokeAllUserTokens(user);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header!");
    }
}
