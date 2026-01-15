package com.exposer.services.implementation;

import com.exposer.dao.interfaces.CommentDao;
import com.exposer.dao.interfaces.PostDao;
import com.exposer.exception.ResourceNotFoundException;
import com.exposer.models.dto.request.CommentRequest;
import com.exposer.models.dto.request.EditCommentRequest;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.response.CommentResponse;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.entity.Comment;
import com.exposer.models.entity.User;
import com.exposer.services.interfaces.CommentService;
import com.exposer.utils.AuthUtils;
import com.exposer.utils.CommonUtil;
import com.exposer.utils.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.exposer.constants.ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
class CommentServiceImpl implements CommentService {

    private final CommentDao commentDao;
    private final PostDao postDao;
    private final AuthUtils authUtils;

    @Override
    @Transactional
    public CommentResponse addComment(String token, CommentRequest commentRequest) {

        log.info("Starting to add comment for postId: {}", commentRequest.getPostId());

        User user = authUtils.getUserFromToken(token);
        log.debug("Retrieved user from token: userId={}, username={}",
                user.getId(), user.getUsername());

        String postId = commentRequest.getPostId();


        checkIfPostExistOrNot(postId);

        log.debug("Post found, creating comment entity");
        Comment comment = Comment.builder()
                .postId(postId)
                .user(user)
                .description(commentRequest.getDescription())
                .build();

        log.info("Saving comment for postId: {} by userId: {}", postId, user.getId());
        Comment saved = commentDao.save(comment);
        log.info("Comment added successfully: commentId={}, postId={}, userId={}",
                saved.getId(), saved.getPostId(), user.getId());

        // update post has comment and increment comment count
        postDao.updatePostForNewComment(postId);

        return CommentMapper.toCommentResponse(saved);
    }

    @Override
    @Transactional
    public CommentResponse replyComment(String token, String commentId, CommentRequest commentRequest) {

        log.info("Starting to add reply to comment: commentId={}", commentId);
        User user = authUtils.getUserFromToken(token);
        log.debug("Retrieved user from token: userId={}", user.getId());

        Comment parentComment = commentDao.findById(commentId).orElseThrow(
                () -> {
                    log.warn("Parent comment not found: commentId={}", commentId);
                    return new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
                }
        );

        if (!parentComment.getPostId().equals(commentRequest.getPostId())) {
            log.warn("Post ID mismatch: parentPostId={}, requestPostId={}, userId={}",
                    parentComment.getPostId(), commentRequest.getPostId(), user.getId());
            throw new IllegalArgumentException("Post ID does not match parent comment's post");
        }

        if (parentComment.isDeleted()) {
            log.warn("Attempted to reply to deleted comment: commentId={}, userId={}",
                    commentId, user.getId());
            throw new IllegalArgumentException("Cannot reply to deleted comments");
        }

        log.debug("Incrementing reply count for parent comment: commentId={}, currentCount={}",
                commentId, parentComment.getReplyCount());

        log.debug("Creating reply comment entity");
        Comment reply = Comment.builder()
                .postId(commentRequest.getPostId())
                .user(user)
                .description(commentRequest.getDescription())
                .parentCommentId(commentId)
                .build();

        log.info("Saving reply comment to parent comment: parentId={}, by userId={}",
                commentId, user.getId());
        Comment savedReply = commentDao.save(reply);

        // Incrementing parent comment
        commentDao.increaseReplyCount(commentId);

        log.debug("Incrementing reply count for parent comment: commentId={}, currentCount={}",
                commentId, parentComment.getReplyCount());

        parentComment.addReply(savedReply);

        log.info("Reply comment added successfully: replyId={}, parentId={}, userId={}",
                savedReply.getId(), commentId, user.getId());

        return CommentMapper.toCommentResponse(savedReply);
    }

    @Override
    @Transactional
    public CommentResponse editComment(String token, String commentId, EditCommentRequest commentRequest) {

        log.info("Starting to edit comment: commentId={}", commentId);

        Comment comment = validateCommentAuthority(token, commentId);

        String description = commentRequest.getDescription();

        if (comment.getCreatedAt().isBefore(Instant.now().minus(15, ChronoUnit.MINUTES))) {
            throw new IllegalArgumentException("You can't edit your comment after 15 minutes of creation");
        }

        if (description == null || description.trim().isEmpty()) {
            log.warn("Empty description provided for edit, returning existing comment: commentId={}", commentId);
            return CommentMapper.toCommentResponse(comment);
        }


        log.debug("Updating comment description, old description length: {}, new description length: {}",
                comment.getDescription() != null ? comment.getDescription().length() : 0,
                description.length());

        comment.setDescription(description);
        comment.setEdited(true);

        log.info("Saving edited comment: commentId={}", commentId);
        Comment saved = commentDao.save(comment);

        log.info("Comment edited successfully: commentId={}", commentId);
        return CommentMapper.toCommentResponse(saved);
    }

    @Override
    public void deleteComment(String token, String commentId) {
        log.info("Starting to delete comment: commentId={}", commentId);
        Comment comment = validateCommentAuthority(token, commentId);

        comment.setDeleted(true);

        log.info("Saving soft-deleted comment: commentId={}", commentId);
        commentDao.save(comment);

        log.info("Comment soft-deleted successfully: commentId={}", commentId);

    }

    @Override
    public PagedResponse<CommentResponse> getAllComments(PaginationRequest paginationRequest) {
        log.debug("Fetching all comments with pagination: page={}, size={}",
                paginationRequest.getPage(), paginationRequest.getSize());
        Page<Comment> comments = commentDao.findAll(paginationRequest);
        log.info("Retrieved {} comments from total {}",
                comments.getNumberOfElements(), comments.getTotalElements());

        return CommonUtil.buildPagedResponse(comments, CommentMapper::toCommentResponse);
    }

    @Override
    public PagedResponse<CommentResponse> getAllCommentByUser(String token, PaginationRequest paginationRequest) {
        log.debug("Fetching comments by user with pagination: page={}, size={}",
                paginationRequest.getPage(), paginationRequest.getSize());

        String userId = authUtils.getUserIdFromToken(token);

        log.debug("Fetching comments for user: userId={}", userId);
        Page<Comment> comments = commentDao.findByUserId(userId, paginationRequest);

        log.info("Retrieved {} comments for user {} from total {}",
                comments.getNumberOfElements(), userId, comments.getTotalElements());

        return CommonUtil.buildPagedResponse(comments, CommentMapper::toCommentResponse);

    }


    @Override
    public CommentResponse getCommentWithReplies(String commentId) {


        log.info("Fetching comment with replies: commentId={}", commentId);

        Comment comment = commentDao.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        List<Comment> replies = commentDao.findRepliesByCommentId(commentId);

        comment.setReplies(replies);

        return CommentMapper.toCommentResponse(comment);
    }


    @Override
    public PagedResponse<CommentResponse> getAllCommentsByPost(String postId, PaginationRequest paginationRequest) {
        log.debug("Fetching comments for post: postId={} with pagination: page={}, size={}",
                postId, paginationRequest.getPage(), paginationRequest.getSize());

        checkIfPostExistOrNot(postId);

        Page<Comment> comments = commentDao.findByPostId(postId, paginationRequest);
        log.info("Retrieved {} comments for post {} from total {}",
                comments.getNumberOfElements(), postId, comments.getTotalElements());

        return CommonUtil.buildPagedResponse(comments, CommentMapper::toCommentResponse);
    }


    @Override
    public PagedResponse<CommentResponse> getAllRepliesComments(String commentId, PaginationRequest paginationRequest) {
        log.debug("Fetching replies for comment: {}  with pagination: page={}, size={}",
                commentId, paginationRequest.getPage(), paginationRequest.getSize());

        Page<Comment> replies = commentDao.findRepliesByComment(commentId, paginationRequest);

        log.info("Retrieved {} replies for comments {} from total {}",
                replies.getNumberOfElements(), commentId, replies.getTotalElements());

        return CommonUtil.buildPagedResponse(replies, CommentMapper::toCommentResponse);
    }

    private Comment validateCommentAuthority(String token, String commentId) {

        String userId = authUtils.getUserIdFromToken(token);
        log.debug("Retrieved userId from token: userId={}", userId);

        log.debug("Fetching comment: commentId={}", commentId);
        Comment comment = commentDao.findById(commentId).orElseThrow(
                () -> {
                    log.warn("Comment not found: commentId={}", commentId);
                    return new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
                }
        );

        log.debug("Validating user authority comment: userId={}, commentOwnerId={}",
                userId, comment.getUser().getId());
        CommentService.validateAuthority(userId, comment);
        return comment;
    }

    private void checkIfPostExistOrNot(String postId) {

        log.debug("Checking if post exists: postId={}", postId);
        boolean exists = postDao.existsById(postId);

        if (!exists) {
            log.warn("Post not found while fetching comments: postId={}", postId);
            throw new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }
    }

}
