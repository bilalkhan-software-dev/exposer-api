package com.exposer.controllers;

import com.exposer.handler.GenericResponseHandler;
import com.exposer.models.dto.request.LoginRequest;
import com.exposer.models.dto.request.RegisterRequest;
import com.exposer.models.dto.response.AuthResponse;
import com.exposer.services.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication management APIs for user registration, login, and email verification")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user account. After registration, a verification email will be sent to the provided email address."
    )
    @PostMapping("/register")
    ResponseEntity<Map<String, Object>> register(
            @Parameter(
                    description = "User registration details",
                    required = true,
                    schema = @Schema(implementation = RegisterRequest.class)
            )
            @Valid @RequestBody RegisterRequest registerRequest) {

        authService.registerUser(registerRequest);
        return GenericResponseHandler.createBuildResponseMessage(
                "Registration successful. Please check your email for verification instructions.",
                HttpStatus.CREATED
        );
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user and returns JWT tokens for subsequent API calls."
    )
    @PostMapping("/login")
    ResponseEntity<Map<String, Object>> login(
            @Parameter(
                    description = "User login credentials",
                    required = true,
                    schema = @Schema(implementation = LoginRequest.class)
            )
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return GenericResponseHandler.createBuildResponse(
                "Login successful",
                response,
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Verify email address",
            description = "Verifies a user's email address using the token sent to their email."
    )
    @GetMapping("/verify-email")
    ResponseEntity<Map<String, Object>> verifyEmail(
            @Parameter(
                    description = "Email address to verify",
                    required = true,
                    example = "user@example.com"
            )
            @RequestParam String email,

            @Parameter(
                    description = "Verification token sent to email",
                    required = true
            )
            @RequestParam("token") String verificationToken) {

        authService.verifyEmail(email, verificationToken);
        return GenericResponseHandler.createBuildResponseMessage(
                "Email verified successfully. Your account is now active.",
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Resend verification email",
            description = "Resends the email verification link to the user's email address."
    )
    @PostMapping("/resend-verification")
    ResponseEntity<Map<String, Object>> resendVerificationEmail(
            @Parameter(
                    description = "Email address to resend verification",
                    required = true,
                    example = "user@example.com"
            )
            @RequestParam String email) {

        authService.resendEmailVerification(email);
        return GenericResponseHandler.createBuildResponseMessage(
                "Verification email resent successfully. Please check your inbox.",
                HttpStatus.OK
        );
    }
}