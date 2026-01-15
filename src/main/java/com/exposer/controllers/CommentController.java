package com.exposer.controllers;

import com.exposer.handler.GenericResponseHandler;
import com.exposer.models.dto.request.CommentRequest;
import com.exposer.models.dto.request.EditCommentRequest;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.response.CommentResponse;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.services.interfaces.CommentService;
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
@RequestMapping("/api/v1/comment")
@Tag(name = "Comment Management", description = "APIs for managing comments and replies")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "Create a new comment",
            description = "Creates a new comment on a post. Requires authentication."
    )
    @PostMapping
    ResponseEntity<Map<String, Object>> createComment(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "Comment creation data",
                    required = true,
                    schema = @Schema(implementation = CommentRequest.class)
            )
            @Valid @RequestBody CommentRequest request) {

        CommentResponse comment = commentService.addComment(token, request);
        return GenericResponseHandler.createBuildResponse("Comment saved successfully", comment, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Reply to a comment",
            description = "Creates a reply to an existing comment. Requires authentication."
    )
    @PostMapping("/reply/{commentId}")
    ResponseEntity<Map<String, Object>> replyComment(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "ID of the comment to reply to",
                    required = true
            )
            @PathVariable String commentId,

            @Parameter(
                    description = "Reply comment data",
                    required = true,
                    schema = @Schema(implementation = CommentRequest.class)
            )
            @Valid @RequestBody CommentRequest request) {

        CommentResponse comment = commentService.replyComment(token, commentId, request);
        return GenericResponseHandler.createBuildResponse("Reply saved successfully", comment, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Edit a comment",
            description = "Updates the content of an existing comment. Only the comment author can edit their comment."
    )
    @PatchMapping("/{commentId}")
    ResponseEntity<Map<String, Object>> editComment(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "ID of the comment to edit",
                    required = true
            )
            @PathVariable String commentId,

            @Parameter(
                    description = "Updated comment content",
                    required = true,
                    schema = @Schema(implementation = EditCommentRequest.class)
            )
            @Valid @RequestBody EditCommentRequest request) {

        CommentResponse comment = commentService.editComment(token, commentId, request);
        return GenericResponseHandler.createBuildResponse("Comment edited successfully", comment, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a comment (Soft Delete)",
            description = "Performs a soft delete on a comment. The comment is marked as deleted but remains in the database. Only the comment author or admin can delete a comment."
    )
    @DeleteMapping("/{commentId}")
    ResponseEntity<Map<String, Object>> removeComment(
            @Parameter(
                    description = "Bearer token for authentication",
                    required = true,
                    in = ParameterIn.HEADER,
                    example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
            )
            @RequestHeader(AUTHORIZATION_HEADER) String token,

            @Parameter(
                    description = "ID of the comment to delete",
                    required = true
            )
            @PathVariable String commentId) {

        commentService.deleteComment(token, commentId);
        return GenericResponseHandler.createBuildResponseMessage("Your comment softly removed successfully", HttpStatus.OK);
    }

    @Operation(
            summary = "Get my comments",
            description = "Retrieves a paginated list of comments made by the currently authenticated user."
    )
    @GetMapping("/")
    ResponseEntity<Map<String, Object>> getMyComments(
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

        PagedResponse<CommentResponse> myComments = commentService.getAllCommentByUser(token, paginationRequest);

        if (myComments.getContent().isEmpty()) {
            return GenericResponseHandler.createBuildResponse("You currently does not have comments", myComments, HttpStatus.NO_CONTENT);
        }

        return GenericResponseHandler.createBuildResponse("Comments retrieved successfully", myComments, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all comments (Admin only)",
            description = "Retrieves a paginated list of all comments in the system. This endpoint is restricted to administrators only."
    )
    @GetMapping()
    @PreAuthorize(ONLY_ADMIN)
    ResponseEntity<Map<String, Object>> getComments(
            @Parameter(
                    description = "Pagination parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest paginationRequest) {

        PagedResponse<CommentResponse> comments = commentService.getAllComments(paginationRequest);

        if (comments.getContent().isEmpty()) {
            return GenericResponseHandler.createBuildResponse("Currently no comments available", comments, HttpStatus.NO_CONTENT);
        }

        return GenericResponseHandler.createBuildResponse("Comments retrieved successfully", comments, HttpStatus.OK);
    }
}