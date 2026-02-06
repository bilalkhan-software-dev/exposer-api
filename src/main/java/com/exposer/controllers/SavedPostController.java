package com.exposer.controllers;

import com.exposer.handler.ResponseHandler;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.request.SavedPostRequest;
import com.exposer.models.dto.response.ApiResponse;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.SavedPostResponse;
import com.exposer.services.interfaces.SavedPostService;
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
@RequestMapping("/api/v1/saved-post")
@Tag(name = "Saved Posts", description = "APIs for saving and managing bookmarked posts")
public class SavedPostController {

    private final SavedPostService savedPostService;

    @Operation(
            summary = "Save a post",
            description = "Adds a post to the user's saved posts/bookmarks for later reading."
    )
    @PostMapping
    ResponseEntity<ApiResponse<SavedPostResponse>> savePost(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "Post to save",
                    required = true,
                    schema = @Schema(implementation = SavedPostRequest.class)
            )
            @Valid @RequestBody SavedPostRequest request) {

        SavedPostResponse post = savedPostService.savePost(token, request);
        return ResponseHandler.createBuildResponse("Post saved successfully", post, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Remove a saved post",
            description = "Removes a post from the user's saved posts/bookmarks."
    )
    @DeleteMapping("/{savedPostId}")
    ResponseEntity<ApiResponse<Void>> removeSavedPost(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "ID of the saved post entry to remove",
                    required = true
            )
            @PathVariable String savedPostId) {

        savedPostService.removeSavedPost(token, savedPostId);
        return ResponseHandler.createBuildResponseMessage(
                "Post removed successfully from saved post history",
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Get all saved posts (Admin only)",
            description = "Retrieves a paginated list of all saved posts across all users. Admin access required."
    )
    @GetMapping()
    @PreAuthorize(ONLY_ADMIN)
    ResponseEntity<ApiResponse<PagedResponse<SavedPostResponse>>> getAllSavedPost(
            @Parameter(
                    description = "Pagination parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest request) {

        PagedResponse<SavedPostResponse> allSavedPosts = savedPostService.getAllSavedPosts(request);
        if (allSavedPosts.getContent().isEmpty()) {
            return ResponseHandler.createBuildResponse(
                    "No saved posts available",
                    allSavedPosts,
                    HttpStatus.NO_CONTENT
            );
        }

        return ResponseHandler.createBuildResponse(
                "Saved Posts retrieved successfully",
                allSavedPosts,
                HttpStatus.OK
        );
    }

    @Operation(
            summary = "Get my saved posts",
            description = "Retrieves a paginated list of posts saved/bookmarked by the currently authenticated user."
    )
    @GetMapping("/my")
    ResponseEntity<ApiResponse<PagedResponse<SavedPostResponse>>> getMySavedPost(
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
            @Valid @ModelAttribute PaginationRequest request) {

        PagedResponse<SavedPostResponse> allSavedPosts = savedPostService.mySavedPost(token, request);
        if (allSavedPosts.getContent().isEmpty()) {
            return ResponseHandler.createBuildResponse(
                    "You currently don't have any saved posts available",
                    allSavedPosts,
                    HttpStatus.NO_CONTENT
            );
        }

        return ResponseHandler.createBuildResponse(
                "Your Saved Posts retrieved successfully",
                allSavedPosts,
                HttpStatus.OK
        );
    }
}