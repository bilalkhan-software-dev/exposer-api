package com.exposer.models.dto.response;

import com.exposer.models.entity.PostStats;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private String id;
    private String title;
    private String content;
    private String image;

    private BasicUserResponse author;

    @Builder.Default
    private Set<String> tags = new HashSet<>();

    private boolean hasComments;

    private boolean status;

    @Builder.Default
    private PostStats postStats = new PostStats();

    private Instant createdAt;
    private Instant updatedAt;


}
