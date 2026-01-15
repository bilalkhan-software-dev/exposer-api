package com.exposer.models.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostStats {

    @Builder.Default
    private long likeCount = 0;

    @Builder.Default
    private long commentCount = 0;

    @Builder.Default
    private long saveCount = 0;
}
