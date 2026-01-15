package com.exposer.services.interfaces;

import com.exposer.exception.AuthenticationException;
import com.exposer.models.dto.request.CreateLikeRequest;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.response.LikeResponse;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.entity.Like;

import static com.exposer.constants.ErrorMessage.UNAUTHENTICATED_ILLEGAL_MESSAGE;


public interface LikeService {

    LikeResponse createLike(String token, CreateLikeRequest createLikeRequest);

    void deleteLike(String token, String likeId);

    PagedResponse<LikeResponse> getLikes(PaginationRequest paginationRequest);

    PagedResponse<LikeResponse> getMyLikes(String token, PaginationRequest paginationRequest);

    PagedResponse<LikeResponse> getLikesByTargetId(String targetId, PaginationRequest paginationRequest);

    static void validateAuthority(String userId, Like like) {

        if (!userId.equals(like.getUserId())) {
            throw new AuthenticationException(UNAUTHENTICATED_ILLEGAL_MESSAGE);
        }

    }

}
