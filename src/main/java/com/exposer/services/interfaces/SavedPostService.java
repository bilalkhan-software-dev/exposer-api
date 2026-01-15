package com.exposer.services.interfaces;

import com.exposer.exception.AuthenticationException;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.request.SavedPostRequest;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.SavedPostResponse;
import com.exposer.models.entity.SavedPost;

import static com.exposer.constants.ErrorMessage.UNAUTHENTICATED_ILLEGAL_MESSAGE;

public interface SavedPostService {

    SavedPostResponse savePost(String token, SavedPostRequest savedPostRequest);

    /**
     * For Admin
     */
    PagedResponse<SavedPostResponse> getAllSavedPosts(PaginationRequest paginationRequest);

    PagedResponse<SavedPostResponse> mySavedPost(String token, PaginationRequest paginationRequest);

    void removeSavedPost(String token, String id);

    static void validateAuthority(String userId, SavedPost savedPost) {

        if (!userId.equals(savedPost.getUserId())) {
            throw new AuthenticationException(UNAUTHENTICATED_ILLEGAL_MESSAGE);
        }

    }


}
