package com.exposer.controllers;

import com.exposer.handler.ResponseHandler;
import com.exposer.models.dto.request.LoginRequest;
import com.exposer.models.dto.request.RegisterRequest;
import com.exposer.models.dto.response.ApiResponse;
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
    ResponseEntity<ApiResponse<Void>> register(
            @Parameter(
                    description = "User registration details",
                    required = true,
                    schema = @Schema(implementation = RegisterRequest.class)
            )
            @Valid @RequestBody RegisterRequest registerRequest) {

        authService.registerUser(registerRequest);
        return ResponseHandler.createBuildResponseMessage(
                "Registration successful. Please check your email for verification instructions.",
                HttpStatus.CREATED
        );
    }

    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user and returns JWT tokens for subsequent API calls."
    )
    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> login(
            @Parameter(
                    description = "User login credentials",
                    required = true,
                    schema = @Schema(implementation = LoginRequest.class)
            )
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseHandler.createBuildResponse(
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
    ResponseEntity<ApiResponse<Void>> verifyEmail(
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
        return ResponseHandler.createBuildResponseMessage(
                "Email verified successfully. Your account is now active.",
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Resend verification email",
            description = "Resends the email verification link to the user's email address."
    )
    @PostMapping("/resend-verification")
    ResponseEntity<ApiResponse<Void>> resendVerificationEmail(
            @Parameter(
                    description = "Email address to resend verification",
                    required = true,
                    example = "user@example.com"
            )
            @RequestParam String email) {

        authService.resendEmailVerification(email);
        return ResponseHandler.createBuildResponseMessage(
                "Verification email resent successfully. Please check your inbox.",
                HttpStatus.OK
        );
    }
}