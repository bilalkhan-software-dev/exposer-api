package com.exposer.services.implementation;

import com.exposer.dao.interfaces.PostDao;
import com.exposer.dao.interfaces.SavedPostDao;
import com.exposer.exception.ExistDataException;
import com.exposer.exception.ResourceNotFoundException;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.request.SavedPostRequest;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.SavedPostResponse;
import com.exposer.models.entity.Post;
import com.exposer.models.entity.SavedPost;
import com.exposer.services.interfaces.SavedPostService;
import com.exposer.utils.AuthUtils;
import com.exposer.utils.CommonUtil;
import com.exposer.utils.mapper.SavedPostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.exposer.constants.ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
class SavedPostServiceImpl implements SavedPostService {

    private final SavedPostDao savedPostDao;
    private final PostDao postDao;
    private final AuthUtils authUtils;

    @Override
    @Transactional
    public SavedPostResponse savePost(String token, SavedPostRequest savedPostRequest) {
        log.info("Starting to save post for user, postId: {}", savedPostRequest.getPostId());

        String userId = authUtils.getUserIdFromToken(token);
        log.debug("Retrieved userId from token: userId={}", userId);

        String postId = savedPostRequest.getPostId();
        log.debug("Checking if post is already saved by user: userId={}, postId={}", userId, postId);

        boolean exists = savedPostDao.existsByUserAndPostId(userId, postId);

        if (exists) {
            log.warn("Post already saved by user: userId={}, postId={}", userId, postId);
            throw new ExistDataException("This post already saved in your history");
        }

        log.debug("Fetching post details: postId={}", postId);
        Post post = postDao.findById(postId).orElseThrow(
                () -> {
                    log.warn("Post not found for saving: postId={}", postId);
                    return new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
                }
        );

        log.debug("Creating SavedPost entity: userId={}, postId={}, notesLength={}",
                userId, postId,
                savedPostRequest.getNotes() != null ? savedPostRequest.getNotes().length() : 0);

        SavedPost savedPost = SavedPost.builder()
                .userId(userId)
                .post(post)
                .notes(savedPostRequest.getNotes())
                .build();

        log.info("Saving post to user's saved list: userId={}, postId={}", userId, postId);
        SavedPost saved = savedPostDao.save(savedPost);

        postDao.incrementSaveCount(postId);

        log.info("Post saved successfully: savedPostId={}, userId={}, postId={}",
                saved.getId(), userId, postId);

        return SavedPostMapper.toSavedPostResponse(saved);
    }

    @Override
    public PagedResponse<SavedPostResponse> getAllSavedPosts(PaginationRequest paginationRequest) {
        log.debug("Fetching all saved posts with pagination: page={}, size={}",
                paginationRequest.getPage(), paginationRequest.getSize());

        log.info("Retrieving all saved posts across all users");
        Page<SavedPost> allSavedPosts = savedPostDao.findAll(paginationRequest);

        log.info("Retrieved {} saved posts from total {} across all users",
                allSavedPosts.getNumberOfElements(), allSavedPosts.getTotalElements());


        return CommonUtil.buildPagedResponse(allSavedPosts, SavedPostMapper::toSavedPostResponse);
    }

    @Override
    public PagedResponse<SavedPostResponse> mySavedPost(String token, PaginationRequest paginationRequest) {
        log.debug("Fetching user's saved posts with pagination: page={}, size={}",
                paginationRequest.getPage(), paginationRequest.getSize());

        String userId = authUtils.getUserIdFromToken(token);


        log.debug("Fetching saved posts for user: userId={}", userId);
        Page<SavedPost> savedPosts = savedPostDao.findByUser(userId, paginationRequest);

        log.info("Retrieved {} saved posts for user {} from total {}",
                savedPosts.getNumberOfElements(), userId, savedPosts.getTotalElements());


        return CommonUtil.buildPagedResponse(savedPosts, SavedPostMapper::toSavedPostResponse);
    }

    @Override
    @Transactional
    public void removeSavedPost(String token, String id) {
        log.info("Starting to remove saved post: savedPostId={}", id);

        String userId = authUtils.getUserIdFromToken(token);

        log.debug("Fetching saved post to delete: savedPostId={}", id);
        SavedPost savedPost = savedPostDao.findById(id).orElseThrow(
                () -> {
                    log.warn("Saved post not found for deletion: savedPostId={}", id);
                    return new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
                }
        );

        log.debug("Validating user authority to delete saved post: userId={}, savedPostUserId={}",
                userId, savedPost.getUserId());
        SavedPostService.validateAuthority(userId, savedPost);

        String postId = savedPost.getPost().getId();
        log.info("Deleting saved post: savedPostId={}, postId={}, userId={}",
                id, postId, userId);

        postDao.decrementSaveCount(postId);

        savedPostDao.deleteById(id);

        log.info("Saved post removed successfully: savedPostId={}, userId={}", id, userId);
    }
}