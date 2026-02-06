package com.exposer.controllers;

import com.exposer.handler.ResponseHandler;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.request.ProfileUpdateRequest;
import com.exposer.models.dto.response.ApiResponse;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.UserResponse;
import com.exposer.models.dto.response.admin.AdminUserResponse;
import com.exposer.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import static com.exposer.constants.AppConstants.AUTHORIZATION_HEADER;
import static com.exposer.constants.AppConstants.ONLY_ADMIN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Validated
@Tag(name = "User Management", description = "APIs for user profile management and administration")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile information of the currently authenticated user."
    )
    @GetMapping()
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token) {

        UserResponse response = userService.getProfile(token);
        return ResponseHandler.createBuildResponse("Your profile retrieved successfully", response, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete user (Admin only)",
            description = "Deletes a user account by username. This operation is restricted to administrators only."
    )
    @DeleteMapping("/{username}")
    @PreAuthorize(ONLY_ADMIN)
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(
                    description = "Username of the user to delete",
                    required = true
            )
            @NotBlank(message = "Username is required") @PathVariable String username) {

        userService.deleteUser(username);
        return ResponseHandler.createBuildResponseMessage("User deleted successfully by username: " + username, HttpStatus.OK);
    }

    @Operation(
            summary = "Update user profile",
            description = "Updates the profile information of the currently authenticated user."
    )
    @PatchMapping
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "Profile update data",
                    required = true,
                    schema = @Schema(implementation = ProfileUpdateRequest.class)
            )
            @Valid @RequestBody ProfileUpdateRequest request) {

        UserResponse response = userService.updateProfile(token, request);
        return ResponseHandler.createBuildResponse("Profile updated successfully", response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all users (Admin only)",
            description = "Retrieves a paginated list of all users with administrative details. This endpoint is restricted to administrators only."
    )
    @PreAuthorize(ONLY_ADMIN)
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<PagedResponse<AdminUserResponse>>> getAllUsers(
            @Parameter(
                    description = "Pagination parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest request) {

        PagedResponse<AdminUserResponse> users = userService.getUsers(request);

        if (users.getContent().isEmpty()) {
            return ResponseHandler.createBuildResponse("No users found", null, HttpStatus.OK);
        }

        return ResponseHandler.createBuildResponse("Users retrieved successfully", users, HttpStatus.OK);
    }
}