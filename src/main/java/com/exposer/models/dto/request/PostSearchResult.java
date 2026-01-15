package com.exposer.models.dto.request;

import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostSearchResult {

    private String id;
    private String title;

    private Instant createdAt;

    @Builder.Default
    private Set<String> tags = new HashSet<>();
}
