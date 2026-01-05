package com.exposer.controllers;


import com.exposer.handler.GenericResponseHandler;
import com.exposer.models.dto.request.ProfileUpdateRequest;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.UserResponse;
import com.exposer.services.interfaces.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.exposer.constants.AppConstants.DEFAULT_PAGE_NUM;
import static com.exposer.constants.AppConstants.DEFAULT_PAGE_SIZE;
import static com.exposer.constants.AppConstants.AUTHORIZATION_HEADER;
import static com.exposer.constants.AppConstants.ONLY_ADMIN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<Map<String, Object>> getProfile(@RequestHeader(AUTHORIZATION_HEADER) String token) {

        UserResponse response = userService.getProfile(token);

        return GenericResponseHandler.createBuildResponse("Your profile retrieved successfully", response, HttpStatus.OK);

    }

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getUserDetails(@NotBlank(message = "Username is required") @PathVariable String username) {

        UserResponse response = userService.getByUsername(username);

        return GenericResponseHandler.createBuildResponse("Your profile retrieved successfully", response, HttpStatus.OK);

    }

    @PatchMapping
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestHeader(AUTHORIZATION_HEADER) String token, @Valid @RequestBody ProfileUpdateRequest request) {

        UserResponse response = userService.updateProfile(token, request);

        return GenericResponseHandler.createBuildResponse("Profile updated successfully", response, HttpStatus.OK);

    }

    @PreAuthorize(ONLY_ADMIN)
    @GetMapping("/admin/all")
    public ResponseEntity<Map<String, Object>> getAllUsers(@RequestParam(required = false, defaultValue = DEFAULT_PAGE_NUM) int page,
                                                           @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE)
                                                           @Max(value = 100, message = "Page size cannot exceed 100")
                                                           int size,
                                                           @RequestParam(required = false, defaultValue = "true") boolean isNewest
    ) {

        PagedResponse<UserResponse> users = userService.getUsers(page, size, isNewest);

        if (users.getContent().isEmpty()) {
            return GenericResponseHandler.createBuildResponse("No users found", null, HttpStatus.OK);
        }

        return GenericResponseHandler.createBuildResponse("Users retrieved successfully", users, HttpStatus.OK);
    }


}
