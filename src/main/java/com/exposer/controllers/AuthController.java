package com.exposer.controllers;

import com.exposer.handler.GenericResponseHandler;
import com.exposer.models.dto.request.LoginRequest;
import com.exposer.models.dto.request.RegisterRequest;
import com.exposer.models.dto.response.AuthResponse;
import com.exposer.services.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterRequest registerRequest) {

        authService.registerUser(registerRequest);
        return GenericResponseHandler.createBuildResponseMessage(
                "Registration successful. Please check your email for verification instructions.",
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return GenericResponseHandler.createBuildResponse(
                "Login successful",
                response,
                HttpStatus.OK
        );
    }

    @GetMapping("/verify-email")
    ResponseEntity<Map<String, Object>> verifyEmail(
            @RequestParam String email,
            @RequestParam("token") String verificationToken
    ) {

        authService.verifyEmail(email, verificationToken);
        return GenericResponseHandler.createBuildResponseMessage(
                "Email verified successfully. Your account is now active.",
                HttpStatus.OK
        );
    }

    @PostMapping("/resend-verification")
    ResponseEntity<Map<String, Object>> resendVerificationEmail(@RequestParam String email) {

        authService.resendEmailVerification(email);
        return GenericResponseHandler.createBuildResponseMessage(
                "Verification email resent successfully. Please check your inbox.",
                HttpStatus.OK
        );
    }
}