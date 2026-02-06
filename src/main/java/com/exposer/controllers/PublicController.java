package com.exposer.controllers;

import com.exposer.handler.ResponseHandler;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.request.PostSearchRequest;
import com.exposer.models.dto.request.PostSearchResult;
import com.exposer.models.dto.response.*;
import com.exposer.services.interfaces.CommentService;
import com.exposer.services.interfaces.LikeService;
import com.exposer.services.interfaces.PostService;
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
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public")
@Tag(name = "Public APIs", description = "Public endpoints accessible without authentication")
public class PublicController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @Operation(
            summary = "Get user details by ID",
            description = "Retrieves public profile information of a user by their ID"
    )
    @GetMapping("/user/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetails(

            @NotBlank(message = "User ID is required") @PathVariable String id) {

        UserResponse response = userService.getById(id);
        return ResponseHandler.createBuildResponse("Details retrieved successfully", response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get user details by Username",
            description = "Retrieves public profile information of a user by their username"
    )
    @GetMapping("/user/{username}/")
    public ResponseEntity<ApiResponse<UserResponse>> getUserDetailsByUsername(
            @NotBlank(message = "Username is required") @PathVariable String username) {

        UserResponse response = userService.getByUsername(username);
        return ResponseHandler.createBuildResponse("Details retrieved successfully", response, HttpStatus.OK);
    }

    @Operation(
            summary = "Get post by ID",
            description = "Retrieves complete details of a post including content, author, and metadata"
    )
    @GetMapping("/post/{postId}")
    ResponseEntity<ApiResponse<PostResponse>> getPostById(
            @PathVariable String postId) {

        PostResponse posts = postService.postById(postId);
        return ResponseHandler.createBuildResponse("Post details retrieved successfully", posts, HttpStatus.OK);
    }

    @Operation(
            summary = "Get recommended posts",
            description = "Retrieves personalized post recommendations based on user interests"
    )
    @GetMapping("/post/recommendation")
    ResponseEntity<ApiResponse<PagedResponse<PostResponse>>> recommendedPost(
            @Parameter(
                    description = "Comma-separated tags of user interests",
                    example = "java,spring,programming",
                    in = ParameterIn.HEADER
            )
            @RequestHeader(value = "X-Interested-Tags", required = false) String tagsHeader,

            @Parameter(
                    description = "Pagination parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest pagination) {

        PagedResponse<PostResponse> recommendedPosts = postService.getRecommendedPost(tagsHeader, pagination);

        if (recommendedPosts.getContent().isEmpty()) {
            return ResponseHandler.createBuildResponse(
                    "No recommendations found. Try different interests.",
                    recommendedPosts,
                    HttpStatus.OK
            );
        }

        return ResponseHandler.createBuildResponse("Recommended post retrieved successfully", recommendedPosts, HttpStatus.OK);
    }

    @Operation(
            summary = "Search posts",
            description = "Search posts by keywords, tags, or other criteria"
    )
    @PostMapping("/post/search")
    ResponseEntity<ApiResponse<PagedResponse<PostSearchResult>>> searchPost(
            @Parameter(
                    description = "Search criteria and filters",
                    required = true,
                    schema = @Schema(implementation = PostSearchRequest.class)
            )
            @Valid @RequestBody PostSearchRequest searchRequest,

            @Parameter(
                    description = "Pagination parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest paginationRequest) {

        PagedResponse<PostSearchResult> searchResults = postService.search(searchRequest, paginationRequest);

        if (searchResults.getContent().isEmpty()) {
            return ResponseHandler.createBuildResponse(
                    "No post found. Try different keyword.",
                    searchResults,
                    HttpStatus.OK
            );
        }

        return ResponseHandler.createBuildResponse("Search results for query: " + searchRequest.getQuery(), searchResults, HttpStatus.OK);
    }

    @Operation(
            summary = "Get likes by target ID",
            description = "Retrieves all likes for a specific post or comment"
    )
    @GetMapping("/like/{targetId}")
    ResponseEntity<ApiResponse<PagedResponse<LikeResponse>>> getMyLikes(
            @PathVariable String targetId,

            @Parameter(
                    description = "Pagination parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest paginationRequest) {

        PagedResponse<LikeResponse> likes = likeService.getLikesByTargetId(targetId, paginationRequest);

        if (likes.getContent().isEmpty()) {
            return ResponseHandler.createBuildResponse("No likes found in the given id", likes, HttpStatus.OK);
        }

        return ResponseHandler.createBuildResponse("Like retrieved successfully", likes, HttpStatus.OK);
    }

    @Operation(
            summary = "Get comments by post ID",
            description = "Retrieves all comments for a specific post"
    )
    @GetMapping("/comments/{postId}")
    ResponseEntity<ApiResponse<PagedResponse<CommentResponse>>> getCommentsOfThePost(
            @PathVariable String postId,

            @Parameter(
                    description = "Pagination parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest paginationRequest) {

        PagedResponse<CommentResponse> comments = commentService.getAllCommentsByPost(postId, paginationRequest);

        if (comments.getContent().isEmpty()) {
            return ResponseHandler.createBuildResponse("No comments found in the given id", comments, HttpStatus.OK);
        }

        return ResponseHandler.createBuildResponse("Comments retrieved successfully", comments, HttpStatus.OK);
    }


    @Operation(
            summary = "Get comment replies",
            description = "Retrieves all replies for a specific comment"
    )
    @GetMapping("/comments/{commentId}/replies")
    ResponseEntity<ApiResponse<CommentResponse>> getRepliesOfTheComment(
            @PathVariable String commentId) {

        CommentResponse comments = commentService.getCommentWithReplies(commentId);

        if (comments.getReplies().isEmpty()) {
            return ResponseHandler.createBuildResponse("No replies available", comments, HttpStatus.OK);
        }

        return ResponseHandler.createBuildResponse("Comment replies retrieved successfully", comments, HttpStatus.OK);
    }

    @Operation(
            summary = "Get comment replies except comment itself",
            description = "Retrieves all replies for a specific comment with pagination support"
    )
    @GetMapping("/comments/replies/{commentId}")
    ResponseEntity<ApiResponse<PagedResponse<CommentResponse>>> getRepliesOfTheCommentExceptParent(
            @PathVariable String commentId,

            @Parameter(description = "Pagination Parameters",
                    schema = @Schema(implementation = PaginationRequest.class)
            )
            @Valid @ModelAttribute PaginationRequest paginationRequest
    ) {


        PagedResponse<CommentResponse> repliesComments = commentService.getAllRepliesComments(commentId, paginationRequest);

        if (repliesComments.getContent().isEmpty()) {
            return ResponseHandler.createBuildResponse("No replies available", repliesComments, HttpStatus.OK);
        }

        return ResponseHandler.createBuildResponse("Comment replies retrieved successfully", repliesComments, HttpStatus.OK);
    }


}