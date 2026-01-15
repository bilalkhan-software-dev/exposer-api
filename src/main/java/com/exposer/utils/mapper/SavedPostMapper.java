package com.exposer.utils.mapper;

import com.exposer.models.dto.response.BasicPostResponse;
import com.exposer.models.dto.response.SavedPostResponse;
import com.exposer.models.entity.Post;
import com.exposer.models.entity.SavedPost;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SavedPostMapper {

    public SavedPostResponse toSavedPostResponse(SavedPost savedPost) {

        if (savedPost == null) {
            return null;
        }

        Post post = savedPost.getPost();

        return SavedPostResponse.builder()
                .id(savedPost.getId())
                .userId(savedPost.getUserId())
                .post(BasicPostResponse.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .image(post.getImage())
                        .createdAt(post.getCreatedAt())
                        .build())
                .createdAt(savedPost.getCreatedAt())
                .build();
    }

}
