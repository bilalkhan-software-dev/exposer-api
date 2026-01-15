package com.exposer.models.dto.response;

import com.exposer.models.entity.CommentStats;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

    private String id;

    private String description;

    private String postId;

    private BasicUserResponse user;

    private String parentCommentId;

    @Builder.Default
    private List<CommentResponse> replies = new ArrayList<>();

    private Instant createdAt;

    private Instant updatedAt;

    private boolean isEdited;

    private boolean isDeleted;

    private int replyCount;

    private CommentStats stats;


}
