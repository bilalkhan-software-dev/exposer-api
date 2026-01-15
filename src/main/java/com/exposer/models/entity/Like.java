package com.exposer.models.entity;

import com.exposer.models.entity.enums.LikeType;
import com.exposer.models.entity.enums.TargetType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;


@Document(collection = "likes")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Like extends AbstractEntity {

    @Indexed
    private String userId;

    /***
     *  Can be postId or commentId
     */
    @Indexed
    private String targetId;

    /**
     * POST or COMMENT
     */
    private TargetType targetType;

    /**
     * LIKE, LOVE, HAHA, etc.
     */
    private LikeType likeType;


}