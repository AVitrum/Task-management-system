package com.vitrum.api.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitrum.api.data.enums.RegistrationSource;
import com.vitrum.api.data.request.AuthenticationRequest;
import com.vitrum.api.data.response.AuthenticationResponse;
import com.vitrum.api.data.request.RegisterRequest;
import com.vitrum.api.data.submodels.Token;
import com.vitrum.api.repositories.TokenRepository;
import com.vitrum.api.data.enums.TokenType;
import com.vitrum.api.data.enums.Role;
import com.vitrum.api.data.models.User;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.config.JwtService;
import com.vitrum.api.services.interfaces.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public String register(RegisterRequest request) {
        try {
            if (!request.getSource().equals(RegistrationSource.GOOGLE))
                request.setSource(RegistrationSource.JWT);

            var user = User.builder()
                    .username(request.getUsername().replaceAll("\\s", "_"))
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.USER)
                    .source(request.getSource())
                    .isBanned(false)
                    .imagePath(request.getImagePath())
                    .build();

            var savedUser = repository.save(user);
            var jwtToken = jwtService.generateToken(user);
            saveUserToken(savedUser, jwtToken);
            return jwtToken;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("User with the same email/username already exists.");
        }
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        User user;

        if (repository.existsByUsername(request.getUsername()))
        user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Wrong username"));
        else if (repository.existsByEmail(request.getUsername()))
            user = repository.findByEmail(request.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("Wrong username"));
        else throw new UsernameNotFoundException("User not found");

        if (!user.isAccountNonLocked())
            throw new IllegalStateException("The account is blocked");

        if (user.getSource().equals(RegistrationSource.GOOGLE))
            throw new IllegalStateException("Only google auth");

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Wrong password");
        }
            var jwtToken = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);

            revokeAllUserTokens(user);
            saveUserToken(user, jwtToken);

            return AuthenticationResponse.builder()
                    .accessToken(jwtToken)
                    .refreshToken(refreshToken)
                    .build();
    }

    @Override
    public String googleAuthenticate(User user) {

        if (!user.isAccountNonLocked())
            throw new IllegalStateException("The account is blocked");

        if (user.getSource().equals(RegistrationSource.JWT)) {
            user.setSource(RegistrationSource.GOOGLE);
            user.setPassword(passwordEncoder.encode("GooglePassword"));
            repository.save(user);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            "GooglePassword"
                    )
            );
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("Wrong password");
        }
        var jwtToken = jwtService.generateToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return jwtToken;
    }

    @Override
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractEmail(refreshToken);
        if (userEmail != null) {
            var user = this.repository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}

