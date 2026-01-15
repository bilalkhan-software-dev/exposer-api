package com.exposer.utils.mapper;

import com.exposer.models.dto.response.CommentResponse;
import com.exposer.models.entity.Comment;
import com.exposer.models.entity.CommentStats;
import com.exposer.models.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentMapper {

    public CommentResponse toCommentResponse(Comment comment) {

        if (comment == null) {
            return null;
        }

        User user = comment.getUser();
        CommentStats stats = comment.getStats();


        return CommentResponse.builder()
                .id(comment.getId())
                .description(comment.getDescription())
                .postId(comment.getPostId())
                .user(UserMapper.toBasicUserResponse(user))
                .parentCommentId(comment.getParentCommentId())
                .isEdited(comment.isEdited())
                .isDeleted(comment.isDeleted())
                .replies(comment.getReplies().stream().map(CommentMapper::toCommentResponse).toList())
                .replyCount(comment.getReplyCount())
                .stats(mapCommentStats(stats))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }


    /**
     * Maps CommentStats entity
     */
    private CommentStats mapCommentStats(CommentStats stats) {

        return CommentStats.builder()
                .likeCount(stats.getLikeCount())
                .reportCount(stats.getReportCount())
                .build();
    }
}