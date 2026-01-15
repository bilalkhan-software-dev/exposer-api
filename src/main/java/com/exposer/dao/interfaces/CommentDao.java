package com.exposer.dao.interfaces;

import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.entity.Comment;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface CommentDao {

    Optional<Comment> findById(String id);

    boolean existsById(String id);

    void deleteById(String id);

    Comment save(Comment t);

    Page<Comment> findAll(PaginationRequest request);

    Page<Comment> findByPostId(String postId, PaginationRequest request);

    Page<Comment> findByUserId(String userId, PaginationRequest request);

    Page<Comment> findRepliesByComment(String parentCommentId, PaginationRequest request);

    List<Comment> findRepliesByCommentId(String parentCommentId);

    void increaseReplyCount(String commentId);

    void incrementLikeCount(String commentId);

    void decrementLikeCount(String commentId);
}
