package com.vitrum.api.credentials.user;

import com.vitrum.api.recoverycode.Recoverycode;
import com.vitrum.api.recoverycode.RecoverycodeRepository;
import com.vitrum.api.config.JwtService;
import com.vitrum.api.dto.Request.ChangePasswordRequest;
import com.vitrum.api.dto.Request.ResetPasswordRequest;
import com.vitrum.api.dto.Response.UserProfileResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.Principal;
import java.time.LocalTime;
import java.util.Random;

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

    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        throw new IllegalArgumentException("Invalid authorization header!");
    }
}
