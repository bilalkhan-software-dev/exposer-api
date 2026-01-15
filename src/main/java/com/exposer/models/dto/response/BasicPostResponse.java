package com.exposer.models.dto.response;


import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BasicPostResponse {

    private String id;

    private String title;

    private String image;

    private Instant createdAt;

}
