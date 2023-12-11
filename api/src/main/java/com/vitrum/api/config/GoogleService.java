package com.vitrum.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vitrum.api.data.enums.RegistrationSource;
import com.vitrum.api.data.models.User;
import com.vitrum.api.data.request.RegisterRequest;
import com.vitrum.api.repositories.UserRepository;
import com.vitrum.api.services.interfaces.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GoogleService extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final ObjectMapper objectMapper;

    private String jwtToken;

    @Value("${frontend.server.url}")
    private String frontendServerUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        if ("google".equals(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId())) {
            DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = principal.getAttributes();

            String email = attributes.getOrDefault("email", "").toString();
            String name = attributes.getOrDefault("name", "").toString();
            String imagePath = attributes.getOrDefault("picture", "").toString();

            Optional<User> existingUser = userRepository.findByEmail(email);

            existingUser.ifPresentOrElse(
                    user -> jwtToken = authenticationService.googleAuthenticate(user),
                    () -> jwtToken = authenticationService.register(
                    RegisterRequest.builder()
                            .email(email)
                            .username(name.replaceAll("\\s", "_"))
                            .password("GooglePassword")
                            .source(RegistrationSource.GOOGLE)
                            .imagePath(imagePath)
                            .build()
            ));

            getAuthentication(userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found")), attributes, oAuth2AuthenticationToken);

            String tokenJson = objectMapper.writeValueAsString(jwtToken);

            response.sendRedirect(frontendServerUrl + "?token=" + tokenJson);
        }
    }

    private static void getAuthentication(User user, Map<String, Object> attributes, OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        DefaultOAuth2User newUser = new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRole().name())),
                attributes,
                "email"
        );
        new OAuth2AuthenticationToken(
                newUser,
                List.of(new SimpleGrantedAuthority(user.getRole().name())),
                oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()
        );
    }
}
