package com.exposer.utils.mapper;

import com.exposer.models.dto.response.LikeResponse;
import com.exposer.models.entity.Like;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LikeMapper {

    public LikeResponse toLikeResponse(Like like) {

        if (like == null) {
            return null;
        }

        return LikeResponse.builder()
                .id(like.getId())
                .userId(like.getUserId())
                .likeType(like.getLikeType())
                .targetId(like.getTargetId())
                .targetType(like.getTargetType())
                .build();
    }

}
