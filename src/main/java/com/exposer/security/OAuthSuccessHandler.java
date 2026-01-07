package com.exposer.security;

import com.exposer.models.dto.response.AuthResponse;
import com.exposer.services.interfaces.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId();

        log.info("=== OAuth2 LOGIN SUCCESS ===");
        log.info("Provider: {} ", registrationId);
        if (oAuth2User != null) {
            log.info("Attributes: {} ", oAuth2User.getAttributes());
        } else {
            log.warn("OAuth2 is getting null");
        }
        log.info("============================");

        ResponseEntity<AuthResponse> handler = authService.handleOAuth2LoginRequest(oAuth2User, registrationId);

        AuthResponse body = handler.getBody();

        if (body == null) {
            throw new NullPointerException("Body is getting null. Please try again later.");
        }

        String jwt = body.getToken();
        String username = body.getUsername();

        String redirectUrl = String.format("%s/oauth2/success?token=%s&username=%s", frontendUrl,
                URLEncoder.encode(jwt, StandardCharsets.UTF_8),
                URLEncoder.encode(username, StandardCharsets.UTF_8));
        response.sendRedirect(redirectUrl);
    }

}
