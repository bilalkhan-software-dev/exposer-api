package com.exposer.controllers;

import com.exposer.handler.GenericResponseHandler;
import com.exposer.models.dto.request.*;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.PostResponse;
import com.exposer.models.dto.response.admin.AdminPostResponse;
import com.exposer.services.interfaces.PostService;
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

import java.util.Map;

import static com.exposer.constants.AppConstants.AUTHORIZATION_HEADER;
import static com.exposer.constants.AppConstants.ONLY_ADMIN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/post")
@Tag(name = "Post Management", description = "APIs for creating, updating, and managing posts")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Publish a new post",
            description = "Creates and publishes a new post. Requires authentication."
    )
    @PostMapping
    ResponseEntity<Map<String, Object>> publishPost(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "Post creation data",
                    required = true,
                    schema = @Schema(implementation = CreatePostRequest.class)
            )
            @Valid @RequestBody CreatePostRequest request) {

        PostResponse post = postService.addPost(token, request);
        return GenericResponseHandler.createBuildResponse("Post published successfully", post, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Edit a post",
            description = "Updates the content of an existing post. Only the post author can edit their post."
    )
    @PatchMapping("/{postId}")
    ResponseEntity<Map<String, Object>> editPost(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "ID of the post to edit",
                    required = true
            )
            @PathVariable String postId,

            @Parameter(
                    description = "Updated post content",
                    required = true,
                    schema = @Schema(implementation = EditPostRequest.class)
            )
            @Valid @RequestBody EditPostRequest request) {

        PostResponse post = postService.editPost(token, postId, request);
        return GenericResponseHandler.createBuildResponse("Post edited successfully", post, HttpStatus.OK);
    }

    @Operation(
            summary = "Update post status",
            description = "Updates the status of a post (e.g., DRAFT, PUBLISHED, ARCHIVED)."
    )
    @PatchMapping()
    ResponseEntity<Map<String, Object>> updatePostStatus(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "Post status update data",
                    required = true,
                    schema = @Schema(implementation = UpdatePostStatusRequest.class)
            )
            @Valid @RequestBody UpdatePostStatusRequest request) {

        PostResponse post = postService.updatePostStatus(token, request);
        return GenericResponseHandler.createBuildResponse("Post updated successfully", post, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a post (Admin only)",
            description = "Permanently deletes a post from the system. This action is irreversible. Admin access required."
    )
    @DeleteMapping("/{postId}")
    @PreAuthorize(ONLY_ADMIN)
    ResponseEntity<Map<String, Object>> deletePost(
            @Parameter(
                    description = "ID of the post to delete",
                    required = true
            )
            @PathVariable String postId) {

        postService.deletePostById(postId);
        return GenericResponseHandler.createBuildResponseMessage("Post permanently deleted successfully", HttpStatus.OK);
    }

    @Operation(
            summary = "Get all posts (Admin only)",
            description = "Retrieves a paginated list of all posts with administrative details. Admin access required."
    )
    @GetMapping()
    @PreAuthorize(ONLY_ADMIN)
    ResponseEntity<Map<String, Object>> getAllPostForAdmin(
            @Parameter(
                    description = "Pagination parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest paginationRequest) {

        PagedResponse<AdminPostResponse> posts = postService.getAllPosts(paginationRequest);

        if (posts.getContent().isEmpty()) {
            return GenericResponseHandler.createBuildResponse("Post not found.", posts, HttpStatus.NOT_FOUND);
        }

        return GenericResponseHandler.createBuildResponse("Posts retrieved successfully", posts, HttpStatus.OK);
    }

    @Operation(
            summary = "Get my posts",
            description = "Retrieves a paginated list of posts created by the currently authenticated user."
    )
    @GetMapping("/")
    ResponseEntity<Map<String, Object>> getMyPost(
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

        PagedResponse<PostResponse> posts = postService.myPost(token, paginationRequest);

        if (posts.getContent().isEmpty()) {
            return GenericResponseHandler.createBuildResponse("Post not found.", posts, HttpStatus.NOT_FOUND);
        }

        return GenericResponseHandler.createBuildResponse("Posts retrieved successfully", posts, HttpStatus.OK);
    }
}