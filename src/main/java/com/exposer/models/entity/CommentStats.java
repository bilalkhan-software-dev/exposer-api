package com.exposer.models.entity;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentStats {

    @Builder.Default
    private long likeCount = 0;

    @Builder.Default
    private long reportCount = 0;
}
