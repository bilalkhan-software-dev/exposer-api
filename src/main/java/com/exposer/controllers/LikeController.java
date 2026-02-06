package com.exposer.controllers;

import com.exposer.handler.ResponseHandler;
import com.exposer.models.dto.request.CreateLikeRequest;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.response.ApiResponse;
import com.exposer.models.dto.response.LikeResponse;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.services.interfaces.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import static com.exposer.constants.AppConstants.AUTHORIZATION_HEADER;
import static com.exposer.constants.AppConstants.ONLY_ADMIN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like")
@Tag(name = "Like Management", description = "APIs for managing likes on posts and comments")
public class LikeController {

    private final LikeService likeService;

    @Operation(
            summary = "Like a post or comment",
            description = "Creates a like on a post or comment. If the user has already liked the target, this may toggle the like status."
    )
    @PostMapping
    ResponseEntity<ApiResponse<LikeResponse>> saveLike(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "Like creation data",
                    required = true,
                    schema = @Schema(implementation = CreateLikeRequest.class)
            )
            @Valid @RequestBody CreateLikeRequest request) {

        LikeResponse like = likeService.createLike(token, request);
        return ResponseHandler.createBuildResponse("Like saved successfully", like, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Remove a like",
            description = "Removes/unlikes a specific like by its ID. Only the user who created the like can remove it."
    )
    @DeleteMapping("/{likeId}")
    ResponseEntity<ApiResponse<LikeResponse>> removeLike(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "ID of the like to remove",
                    required = true
            )
            @PathVariable String likeId) {

        likeService.deleteLike(token, likeId);
        return ResponseHandler.createBuildResponseMessage("Like removed successfully", HttpStatus.OK);
    }

    @Operation(
            summary = "Get my likes",
            description = "Retrieves a paginated list of likes created by the currently authenticated user."
    )
    @GetMapping("/")
    ResponseEntity<ApiResponse<PagedResponse<LikeResponse>>> getMyLikes(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "Pagination parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest paginationRequest) {

        PagedResponse<LikeResponse> myLikes = likeService.getMyLikes(token, paginationRequest);

        if (myLikes.getContent().isEmpty()) {
            return ResponseHandler.createBuildResponse("You currently do not have any likes", myLikes, HttpStatus.NO_CONTENT);
        }

        return ResponseHandler.createBuildResponse("Likes retrieved successfully", myLikes, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all likes (Admin only)",
            description = "Retrieves a paginated list of all likes in the system. This endpoint is restricted to administrators only."
    )
    @GetMapping()
    @PreAuthorize(ONLY_ADMIN)
    ResponseEntity<ApiResponse<PagedResponse<LikeResponse>>> getLikes(
            @Parameter(
                    description = "Pagination parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest paginationRequest) {

        PagedResponse<LikeResponse> likes = likeService.getLikes(paginationRequest);

        if (likes.getContent().isEmpty()) {
            return ResponseHandler.createBuildResponse("Currently no likes available", likes, HttpStatus.NO_CONTENT);
        }

        return ResponseHandler.createBuildResponse("Likes retrieved successfully", likes, HttpStatus.OK);
    }
}