package com.exposer.services.interfaces;

import com.exposer.exception.AuthenticationException;
import com.exposer.models.dto.request.LoginRequest;
import com.exposer.models.dto.request.RegisterRequest;
import com.exposer.models.dto.response.AuthResponse;
import com.exposer.models.entity.User;
import com.exposer.models.entity.enums.AccountStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;

public interface AuthService {

    void registerUser(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    static void validateOAuthRequest(OAuth2User oAuth2User, String registrationId) {

        if (oAuth2User == null) throw new IllegalArgumentException("OAuth Request is null");

        if (registrationId == null) throw new IllegalArgumentException("Registration Id is null");

    }

    static void validateUserAccountStatus(User user) {
        switch (user.getAccountStatus()) {
            case UNVERIFIED ->
                    throw new AuthenticationException("Your Account is Unverified. Please verify your account");
            case ACTIVE, VERIFIED -> {
                // No need
            }
            case BANNED -> throw new AuthenticationException("Your Account is Banned By Admin.");
            case DEACTIVATED ->
                    throw new AuthenticationException("Your Account is deactivate. If you want to user please activate your account");

            case null -> throw new NullPointerException("Account Status is null");
        }
    }

    static void validateVerificationToken(String verificationToken, LocalDateTime createAt, AccountStatus accountStatus) {
        if (accountStatus.equals(AccountStatus.VERIFIED) || accountStatus.equals(AccountStatus.ACTIVE)) {
            throw new IllegalArgumentException("Your account is already verified");
        }

        if (verificationToken == null || createAt == null) {
            throw new IllegalArgumentException("No token is generated with this email");
        }

        if (LocalDateTime.now().isAfter(createAt.plusMinutes(30))) {
            throw new AuthenticationException("Verification link is expired. Generate new one");
        }

    }

    ResponseEntity<AuthResponse> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId);

    void verifyEmail(String email, String verificationToken);

    void resendEmailVerification(String email);
}
