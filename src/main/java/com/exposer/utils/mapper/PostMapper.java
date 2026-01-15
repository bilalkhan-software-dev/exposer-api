package com.exposer.utils.mapper;

import com.exposer.models.dto.request.PostSearchResult;
import com.exposer.models.dto.response.PostResponse;
import com.exposer.models.dto.response.admin.AdminPostResponse;
import com.exposer.models.entity.Post;
import com.exposer.models.entity.PostStats;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PostMapper {

    public PostResponse toPostResponse(Post post) {

        if (post == null) {
            return null;
        }

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .image(post.getImage())
                .author(UserMapper.toBasicUserResponse(post.getAuthor()))
                .tags(post.getTags())
                .hasComments(post.isHasComments())
                .status(post.isActive())
                .postStats(PostStats.builder()
                        .commentCount(post.getPostStats().getCommentCount())
                        .likeCount(post.getPostStats().getLikeCount())
                        .saveCount(post.getPostStats().getSaveCount())
                        .build())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }


    public PostSearchResult toPostSearchResponse(Post post) {

        if (post == null) {
            return null;
        }

        return PostSearchResult.builder()
                .id(post.getId())
                .title(post.getTitle())
                .tags(post.getTags())
                .createdAt(post.getCreatedAt())
                .build();
    }


    public AdminPostResponse toAdminPostResponse(Post post) {

        if (post == null) {
            return null;
        }

        return AdminPostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .image(post.getImage())
                .author(UserMapper.toBasicUserResponse(post.getAuthor()))
                .tags(post.getTags())
                .hasComments(post.isHasComments())
                .status(post.isActive())
                .postStats(PostStats.builder()
                        .commentCount(post.getPostStats().getCommentCount())
                        .likeCount(post.getPostStats().getLikeCount())
                        .saveCount(post.getPostStats().getSaveCount())
                        .build())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .createdBy(post.getCreatedBy())
                .updatedBy(post.getUpdatedBy())
                .build();
    }


}
