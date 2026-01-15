package com.exposer.services.implementation;

import com.exposer.dao.interfaces.PostDao;
import com.exposer.exception.ResourceNotFoundException;
import com.exposer.models.dto.request.*;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.PostResponse;
import com.exposer.models.dto.response.admin.AdminPostResponse;
import com.exposer.models.entity.Post;
import com.exposer.models.entity.User;
import com.exposer.services.interfaces.PostService;
import com.exposer.utils.AuthUtils;
import com.exposer.utils.CommonUtil;
import com.exposer.utils.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.exposer.constants.ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
class PostServiceImpl implements PostService {

    private final PostDao postDao;
    private final AuthUtils authUtils;


    @Override
    @Transactional
    public PostResponse addPost(String token, CreatePostRequest request) {
        log.info("Starting to add new post. Title: {}", request.getTitle());
        log.debug("CreatePostRequest details - Title: {}, Content length: {}, Tags count: {}",
                request.getTitle(),
                request.getContent() != null ? request.getContent().length() : 0,
                request.getTags() != null ? request.getTags().size() : 0);

        User user = authUtils.getUserFromToken(token);
        log.debug("Retrieved user from token: {}", user.getId());

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .image(request.getImage())
                .tags(request.getTags())
                .author(user)
                .build();

        log.info("Attempting to save post to database. Post ID will be generated.");
        Post saved = postDao.save(post);
        log.info("Post saved successfully. Generated Post ID: {}", saved.getId());
        log.debug("Saved post details - ID: {}, Title: {}, Author ID: {}, Active: {}",
                saved.getId(), saved.getTitle(), saved.getAuthor().getId(), saved.isActive());

        return PostMapper.toPostResponse(saved);
    }

    @Transactional
    @Override
    public PostResponse editPost(String token, String postId, EditPostRequest request) {

        log.info("Starting to edit post. Post ID: {}", postId);
        User user = authUtils.getUserFromToken(token);
        log.debug("Retrieved user from token for edit operation. User ID: {}", user.getId());

        Post post = postDao.findById(postId).orElseThrow(
                () -> {
                    log.error("Post not found for editing. Post ID: {}", postId);
                    return new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
                }
        );

        log.debug("Found post for editing. Post ID: {}, Author ID: {}, Current status: {}",
                post.getId(), post.getAuthor().getId(), post.isActive());


        PostService.validateAuthority(user, post);
        log.debug("Authority validation passed for user: {} on post: {}", user.getId(), postId);

        Optional.ofNullable(request.getContent()).ifPresent(post::setContent);
        Optional.ofNullable(request.getTitle()).ifPresent(post::setTitle);
        Optional.ofNullable(request.getThumbnail()).ifPresent(post::setImage);
        Optional.ofNullable(request.getIsActive()).ifPresent(post::setActive);

        Optional.ofNullable(request.getTags())
                .filter(tags -> !tags.isEmpty())
                .ifPresent(post::setTags);

        Post updated = postDao.save(post);
        log.info("Post updated successfully. Post ID: {}", updated.getId());

        return PostMapper.toPostResponse(updated);

    }

    @Transactional
    @Override
    public PostResponse updatePostStatus(String token, UpdatePostStatusRequest request) {

        String postId = request.getPostId();
        log.info("Starting to update post status. Post ID: {}, Requested status: {}",
                postId, request.getStatus());


        User user = authUtils.getUserFromToken(token);
        log.debug("Retrieved user from token for status update. User ID: {}", user.getId());

        Post post = postDao.findById(postId).orElseThrow(
                () -> {
                    log.error("Post not found for status update. Post ID: {}", postId);
                    return new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
                }
        );

        log.debug("Found post for status update. Current status: {}, Author ID: {}",
                post.isActive(), post.getAuthor().getId());

        PostService.validateAuthority(user, post);

        log.debug("Authority validation passed for status update on post: {}", postId);

        if (post.isActive() == request.getStatus()) {
            log.info("Post status unchanged. Current and requested status are the same: {}",
                    post.isActive());
            return PostMapper.toPostResponse(postDao.save(post));
        }

        log.info("Changing post status from {} to {}", post.isActive(), request.getStatus());
        post.setActive(request.getStatus());

        Post updated = postDao.save(post);
        log.info("Post status updated successfully. Post ID: {}, New status: {}",
                updated.getId(), updated.isActive());

        return PostMapper.toPostResponse(updated);
    }

    @Override
    public PagedResponse<AdminPostResponse> getAllPosts(PaginationRequest paginationRequest) {
        log.info("Retrieving all posts with pagination. Page: {}, Size: {}, Sort: {}",
                paginationRequest.getPage(),
                paginationRequest.getSize(),
                paginationRequest.getSortBy());

        Page<Post> page = postDao.findAll(paginationRequest);

        log.info("Retrieved {} posts (total: {}, pages: {}, current page: {})",
                page.getNumberOfElements(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber());
        return CommonUtil.buildPagedResponse(page, PostMapper::toAdminPostResponse);
    }

    @Override
    public PagedResponse<PostResponse> myPost(String token, PaginationRequest request) {
        log.info("Retrieving user's own posts with pagination. Page: {}, Size: {}",
                request.getPage(), request.getSize());

        User user = authUtils.getUserFromToken(token);
        log.debug("Retrieved user from token. User ID: {}", user.getId());

        Page<Post> page = postDao.findByUser(user.getId(), request);

        log.info("Retrieved {} posts for user {} (total: {}, pages: {}, current page: {})",
                page.getNumberOfElements(),
                user.getId(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber());

        return CommonUtil.buildPagedResponse(page, PostMapper::toPostResponse);

    }

    @Override
    public PostResponse postById(String postId) {

        log.info("Retrieving post by ID: {}", postId);

        Post post = postDao.findById(postId).orElseThrow(
                () -> {
                    log.error("Post not found by ID: {}", postId);
                    return new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
                }
        );

        log.info("Post retrieved successfully. ID: {}, Title: {}, Status: {}",
                post.getId(), post.getTitle(), post.isActive());
        return PostMapper.toPostResponse(post);

    }

    @Override
    public void deletePostById(String postId) {

        log.info("Attempting to delete post. Post ID: {}", postId);
        boolean exists = postDao.existsById(postId);
        if (!exists) {
            log.error("Cannot delete post. Post not found with ID: {}", postId);
            throw new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }

        log.debug("Post exists. Proceeding with deletion.");
        postDao.deleteById(postId);
        log.info("Post deleted successfully. Post ID: {}", postId);
    }

    @Override
    public PagedResponse<PostSearchResult> search(PostSearchRequest request, PaginationRequest paginationRequest) {
        log.info("Retrieving search posts with pagination. Query: {} tags: {} Page: {}, Size: {}, Sort: {}",
                request.getQuery(),
                request.getTags(),
                paginationRequest.getPage(),
                paginationRequest.getSize(),
                paginationRequest.getSortBy());
        Page<Post> search = postDao.search(request, paginationRequest);
        log.info("Search result {} posts (total: {}, pages: {}, current page: {})",
                search.getNumberOfElements(),
                search.getTotalElements(),
                search.getTotalPages(),
                search.getNumber());

        return CommonUtil.buildPagedResponse(search, PostMapper::toPostSearchResponse);

    }

    @Override
    public PagedResponse<PostResponse> getRecommendedPost(String interestTagHeader, PaginationRequest paginationRequest) {

        // if no tag is provided then recommendation based on default tag
        Set<String> tags = CommonUtil.parseCommaSeparatedTags(interestTagHeader);

        Page<Post> recommendation = postDao.recommendation(tags, paginationRequest);

        return CommonUtil.buildPagedResponse(recommendation, PostMapper::toPostResponse);
    }


}
