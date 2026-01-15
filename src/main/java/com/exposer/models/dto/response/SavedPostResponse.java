package com.exposer.models.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SavedPostResponse {

    private String id;

    private String userId;

    private BasicPostResponse post;

    private Instant createdAt;


}
