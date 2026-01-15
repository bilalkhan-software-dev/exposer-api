package com.exposer.models.entity;


import lombok.*;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Document(collection = "comments")
public class Comment extends AbstractEntity {

    private String description;

    @Indexed
    private String postId;

    @DBRef(lazy = true)
    private User user;

    @Indexed
    private String parentCommentId;

    @Transient
    @Builder.Default
    private List<Comment> replies = new ArrayList<>();

    @Builder.Default
    private int replyCount = 0;

    @Builder.Default
    private CommentStats stats = new CommentStats();

    @Builder.Default
    private boolean isEdited = false;

    @Builder.Default
    private boolean isDeleted = false;

    public void addReply(Comment reply) {
        if (this.replies == null) {
            this.replies = new ArrayList<>();
        }
        this.replies.add(reply);
        this.replyCount = this.replies.size();
    }

}
