package com.exposer.services.implementation;

import com.exposer.dao.interfaces.CommentDao;
import com.exposer.dao.interfaces.LikeDao;
import com.exposer.dao.interfaces.PostDao;
import com.exposer.exception.ExistDataException;
import com.exposer.exception.ResourceNotFoundException;
import com.exposer.models.dto.request.CreateLikeRequest;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.response.LikeResponse;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.entity.Like;
import com.exposer.services.interfaces.LikeService;
import com.exposer.utils.AuthUtils;
import com.exposer.utils.CommonUtil;
import com.exposer.utils.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.exposer.constants.ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
class LikeServiceImpl implements LikeService {

    private final LikeDao likeDao;
    private final PostDao postDao;
    private final CommentDao commentDao;
    private final AuthUtils authUtils;

    @Transactional
    @Override
    public LikeResponse createLike(String token, CreateLikeRequest createLikeRequest) {
        log.info("Starting to create like for target: targetId={}, targetType={}, likeType={}",
                createLikeRequest.getTargetId(),
                createLikeRequest.getTargetType(),
                createLikeRequest.getLikeType());

        String userId = authUtils.getUserIdFromToken(token);
        log.debug("Retrieved userId from token: userId={}", userId);

        String targetId = createLikeRequest.getTargetId();
        log.debug("Checking if like already exists: userId={}, targetId={}", userId, targetId);

        validateTargetId(createLikeRequest);

        boolean exists = likeDao.existsByUserIdAndTargetId(userId, targetId);
        if (exists) {
            log.warn("User already liked this target: userId={}, targetId={}", userId, targetId);
            throw new ExistDataException("Already liked");
        }


        log.debug("Creating Like entity: userId={}, targetId={}, targetType={}, likeType={}",
                userId, targetId, createLikeRequest.getTargetType(), createLikeRequest.getLikeType());

        Like like = Like.builder()
                .userId(userId)
                .targetId(targetId)
                .likeType(createLikeRequest.getLikeType())
                .targetType(createLikeRequest.getTargetType())
                .build();

        log.info("Saving like: userId={}, targetId={}", userId, targetId);
        Like liked = likeDao.save(like);

        // update like count in post/comment
        incrementLikeForPostOrComment(liked);

        log.info("Like created successfully: likeId={}, userId={}, targetId={}, targetType={}",
                liked.getId(), userId, targetId, liked.getTargetType());

        return LikeMapper.toLikeResponse(liked);
    }

    @Override
    public void deleteLike(String token, String likeId) {
        log.info("Starting to delete like: likeId={}", likeId);

        String userId = authUtils.getUserIdFromToken(token);

        log.debug("Fetching like to delete: likeId={}", likeId);
        Like like = likeDao.findById(likeId).orElseThrow(
                () -> {
                    log.warn("Like not found for deletion: likeId={}", likeId);
                    return new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
                }
        );

        log.debug("Validating user authority to delete like: userId={}, likeUserId={}",
                userId, like.getUserId());
        LikeService.validateAuthority(userId, like);

        log.info("Deleting like: likeId={}, userId={}, targetId={}",
                likeId, userId, like.getTargetId());

        // update like count in post/comment
        decrementLikeForPostOrComment(like);

        likeDao.deleteById(likeId);


        log.info("Like deleted successfully: likeId={}, userId={}, targetId={}",
                likeId, userId, like.getTargetId());
    }

    @Override
    public PagedResponse<LikeResponse> getLikes(PaginationRequest paginationRequest) {
        log.debug("Fetching all likes with pagination: page={}, size={}",
                paginationRequest.getPage(), paginationRequest.getSize());

        log.info("Retrieving all likes across all targets and users");
        Page<Like> likes = likeDao.findAll(paginationRequest);

        log.info("Retrieved {} likes from total {} across all targets",
                likes.getNumberOfElements(), likes.getTotalElements());

        return CommonUtil.buildPagedResponse(likes, LikeMapper::toLikeResponse);
    }

    @Override
    public PagedResponse<LikeResponse> getMyLikes(String token, PaginationRequest paginationRequest) {
        log.debug("Fetching user's likes with pagination: page={}, size={}",
                paginationRequest.getPage(), paginationRequest.getSize());

        String userId = authUtils.getUserIdFromToken(token);

        log.debug("Fetching likes for user: userId={}", userId);
        Page<Like> myLikes = likeDao.findByUserId(userId, paginationRequest);

        log.info("Retrieved {} likes for user {} from total {}",
                myLikes.getNumberOfElements(), userId, myLikes.getTotalElements());


        return CommonUtil.buildPagedResponse(myLikes, LikeMapper::toLikeResponse);
    }

    @Override
    public PagedResponse<LikeResponse> getLikesByTargetId(String targetId, PaginationRequest paginationRequest) {
        log.debug("Fetching likes for target: targetId={} with pagination: page={}, size={}",
                targetId, paginationRequest.getPage(), paginationRequest.getSize());

        Page<Like> likesOfTheTargetId = likeDao.findByTargetId(targetId, paginationRequest);

        log.info("Retrieved {} likes for target {} from total {}",
                likesOfTheTargetId.getNumberOfElements(), targetId, likesOfTheTargetId.getTotalElements());


        return CommonUtil.buildPagedResponse(likesOfTheTargetId, LikeMapper::toLikeResponse);
    }


    private void decrementLikeForPostOrComment(Like like) {

        switch (like.getTargetType()) {
            case POST -> postDao.decrementLikeCount(like.getTargetId());
            case COMMENT -> commentDao.decrementLikeCount(like.getTargetId());
            case null -> log.info("Like type is getting null. Skipping decrementing.");
            default -> throw new IllegalArgumentException("Un supported Like Type");
        }

    }

    private void incrementLikeForPostOrComment(Like like) {

        switch (like.getTargetType()) {
            case POST -> postDao.incrementLikeCount(like.getTargetId());
            case COMMENT -> commentDao.incrementLikeCount(like.getTargetId());
            case null -> log.info("Like type is getting null. Skipping incrementing.");
            default -> throw new IllegalArgumentException("Un-Supported Like Type");
        }

    }

    private void validateTargetId(CreateLikeRequest request) {

        boolean isExists;
        String targetId = request.getTargetId();

        switch (request.getTargetType()) {
            case POST -> isExists = postDao.existsById(targetId);
            case COMMENT -> isExists = commentDao.existsById(targetId);
            default -> throw new IllegalArgumentException("Un-Supported Target Type");
        }

        if (!isExists) {
            throw new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }
    }
}