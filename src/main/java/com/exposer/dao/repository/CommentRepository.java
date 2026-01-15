package com.exposer.dao.repository;

import com.exposer.models.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {

    Page<Comment> findByPostId(String postId, Pageable pageable);

    Page<Comment> findByUserId(String userId, Pageable pageable);

    List<Comment> findByParentCommentId(String parentCommentId);

    Page<Comment> findByParentCommentId(String parentCommentId, Pageable pageable);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'replyCount': 1 } }")
    void incrementReplyCount(String commentId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'stats.likeCount': 1 } }")
    void incrementLikeCount(String commentId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'stats.likeCount': -1 } }")
    void decrementLikeCount(String commentId);


}
