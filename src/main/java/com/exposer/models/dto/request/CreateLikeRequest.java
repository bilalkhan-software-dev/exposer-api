package com.exposer.models.dto.request;

import com.exposer.models.entity.enums.LikeType;
import com.exposer.models.entity.enums.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateLikeRequest {

    @NotBlank(message = "Target id is required")
    private String targetId;

    @NotNull(message = "Target type is required (e.g: POST, COMMENT)")
    private TargetType targetType;

    @NotNull(message = "Like type is required (e.g: LIKE, LOVE, HAHA, WOW, SAD, ANGRY)")
    private LikeType likeType;


}
