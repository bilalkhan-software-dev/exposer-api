package com.exposer.services.interfaces;

import com.exposer.exception.AuthenticationException;
import com.exposer.models.dto.request.*;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.PostResponse;
import com.exposer.models.dto.response.admin.AdminPostResponse;
import com.exposer.models.entity.Post;
import com.exposer.models.entity.User;


import static com.exposer.constants.ErrorMessage.UNAUTHENTICATED_ILLEGAL_MESSAGE;

public interface PostService {

    PostResponse addPost(String token, CreatePostRequest request);

    PostResponse editPost(String token, String postId, EditPostRequest request);

    PostResponse updatePostStatus(String token, UpdatePostStatusRequest request);

    PagedResponse<AdminPostResponse> getAllPosts(PaginationRequest paginationRequest);

    PagedResponse<PostResponse> myPost(String token, PaginationRequest request);

    PostResponse postById(String postId);

    void deletePostById(String postId);

    static void validateAuthority(User user, Post post) {

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AuthenticationException(UNAUTHENTICATED_ILLEGAL_MESSAGE);
        }

    }

    PagedResponse<PostSearchResult> search(PostSearchRequest request, PaginationRequest paginationRequest);

    PagedResponse<PostResponse> getRecommendedPost(String interestTagHeader, PaginationRequest paginationRequest);


}
