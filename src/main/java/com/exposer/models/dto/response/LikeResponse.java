package com.exposer.models.dto.response;

import com.exposer.models.entity.enums.LikeType;
import com.exposer.models.entity.enums.TargetType;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponse {

    private String id;

    private String userId;

    private String targetId;

    private TargetType targetType;

    private LikeType likeType;

}
