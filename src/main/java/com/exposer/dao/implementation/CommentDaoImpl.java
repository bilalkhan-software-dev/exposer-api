package com.exposer.dao.implementation;

import com.exposer.dao.interfaces.CommentDao;
import com.exposer.dao.interfaces.RedisCacheService;
import com.exposer.dao.repository.CommentRepository;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.entity.Comment;
import com.exposer.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
class CommentDaoImpl implements CommentDao {

    private final CommentRepository commentRepository;
    private final RedisCacheService redisCacheService;

    @Override
    public Optional<Comment> findById(String id) {

        return redisCacheService.getById(id, Comment.class)
                .or(() -> commentRepository.findById(id))
                .map(comment -> {
                            redisCacheService.putById(id, comment, null);
                            return comment;
                        }
                );
    }

    @Override
    public boolean existsById(String id) {
        return commentRepository.existsById(id);
    }

    @Override
    public void deleteById(String id) {
        commentRepository.deleteById(id);
        redisCacheService.deleteById(id, Comment.class);
    }

    @Override
    public Comment save(Comment comment) {
        Comment saved = commentRepository.save(comment);

        // if any comment added, then increment version post comments cache
        redisCacheService.incrementPaginationVersion(saved.getPostId(), "1");
        return saved;

    }

    @Override
    public Page<Comment> findAll(PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        return commentRepository.findAll(pageable);
    }

    @Override
    public Page<Comment> findByPostId(String postId, PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        Page<Comment> comments = commentRepository.findByPostId(postId, pageable);

        redisCacheService.putById(postId, comments.getContent(), Duration.ofMinutes(5));
        return comments;
    }

    @Override
    public Page<Comment> findByUserId(String userId, PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        return commentRepository.findByUserId(userId, pageable);
    }

    /**
     * Return all replies except parent comment
     */
    @Override
    public Page<Comment> findRepliesByComment(String parentCommentId, PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        return commentRepository.findByParentCommentId(parentCommentId, pageable);
    }

    /**
     * Return all replies with parent comment
     */
    @Override
    public List<Comment> findRepliesByCommentId(String parentCommentId) {

        return commentRepository.findByParentCommentId(parentCommentId);
    }

    @Override
    public void increaseReplyCount(String commentId) {

        commentRepository.incrementReplyCount(commentId);
    }

    @Override
    public void incrementLikeCount(String commentId) {
        commentRepository.incrementLikeCount(commentId);
    }

    @Override
    public void decrementLikeCount(String commentId) {
        commentRepository.decrementLikeCount(commentId);
    }


}
