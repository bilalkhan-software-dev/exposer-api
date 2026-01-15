package com.exposer.services.interfaces;

import com.exposer.exception.AuthenticationException;
import com.exposer.models.dto.request.CommentRequest;
import com.exposer.models.dto.request.EditCommentRequest;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.response.CommentResponse;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.entity.Comment;

import static com.exposer.constants.ErrorMessage.UNAUTHENTICATED_ILLEGAL_MESSAGE;

public interface CommentService {

    CommentResponse addComment(String token, CommentRequest commentRequest);

    CommentResponse replyComment(String comment, String token, CommentRequest commentRequest);

    CommentResponse editComment(String token, String commentId, EditCommentRequest commentRequest);

    void deleteComment(String token, String commentId);

    PagedResponse<CommentResponse> getAllComments(PaginationRequest paginationRequest);

    PagedResponse<CommentResponse> getAllCommentByUser(String token, PaginationRequest paginationRequest);

    CommentResponse getCommentWithReplies(String commentId);

    PagedResponse<CommentResponse> getAllCommentsByPost(String postId, PaginationRequest paginationRequest);

    static void validateAuthority(String userId, Comment comment) {

        if (!userId.equals(comment.getUser().getId())) {
            throw new AuthenticationException(UNAUTHENTICATED_ILLEGAL_MESSAGE);
        }

    }

    PagedResponse<CommentResponse> getAllRepliesComments(String commentId, PaginationRequest paginationRequest);
}
