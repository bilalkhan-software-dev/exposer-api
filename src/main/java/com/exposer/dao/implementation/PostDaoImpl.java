package com.exposer.dao.implementation;

import com.exposer.dao.interfaces.PostDao;
import com.exposer.dao.interfaces.RedisCacheService;
import com.exposer.dao.repository.PostRepository;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.request.PostSearchRequest;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.PostResponse;
import com.exposer.models.dto.response.admin.AdminPostResponse;
import com.exposer.models.entity.Post;
import com.exposer.utils.CommonUtil;
import com.exposer.utils.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.exposer.constants.RedisConstants.POST_CACHE_PREFIX;

@Slf4j
@Repository
@RequiredArgsConstructor
class PostDaoImpl implements PostDao {

    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;
    private final RedisCacheService redisCacheService;

    @Override
    public Optional<Post> findById(String id) {
        return redisCacheService.getById(id, Post.class)
                .or(() -> postRepository.findById(id))
                .map(user -> {
                    redisCacheService.putById(id, user, null);
                    return user;
                });
    }

    @Override
    public boolean existsById(String s) {
        return postRepository.existsById(s);
    }

    @Override
    public void deleteById(String s) {
        postRepository.deleteById(s);
        redisCacheService.deleteById(s, Post.class);
    }

    @Override
    public Post save(Post post) {
        Post saved = postRepository.save(post);

        // Save in cache
        redisCacheService.putById(saved.getId(), saved, Duration.ofMinutes(30));

        // Increment pagination version if it's belongs to the author's post present in cache
        redisCacheService.incrementPaginationVersion(saved.getAuthor().getId(), POST_CACHE_PREFIX);
        return saved;
    }

    @Override
    public PagedResponse<AdminPostResponse> findAll(PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        Page<Post> page = postRepository.findAll(pageable);

        return CommonUtil.buildPagedResponse(page, PostMapper::toAdminPostResponse);

    }

    @Override
    public PagedResponse<PostResponse> findByUser(String userId, PaginationRequest request) {

        PagedResponse<PostResponse> cached = redisCacheService.getByPagination(userId, request, POST_CACHE_PREFIX, new TypeReference<>() {
        });

        if (cached != null && !cached.getContent().isEmpty()) return cached;

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);
        Page<Post> page = postRepository.findByAuthor_Id(userId, pageable);

        PagedResponse<PostResponse> posts = CommonUtil.buildPagedResponse(page, PostMapper::toPostResponse);
        redisCacheService.putByPagination(userId, request, POST_CACHE_PREFIX, posts, Duration.ofMinutes(10));

        return posts;
    }

    @Override
    public Page<Post> search(PostSearchRequest searchRequest, PaginationRequest paginationRequest) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(paginationRequest);

        String query = searchRequest.getQuery();
        Set<String> tags = searchRequest.getTags();
        log.info("Calling repository with: query='{}', tags={}", query, tags);


        Page<Post> result = null;

        if (query != null && !query.trim().isEmpty() && tags != null && !tags.isEmpty()) {
            result = postRepository.findProjectedByTitleAndTags(query, tags, pageable);
        } else if (query != null) {
            result = postRepository.findProjectedByTitle(query, pageable);
        } else if (tags != null && !tags.isEmpty()) {
            result = postRepository.findProjectedByTags(tags, pageable);
        }


        return result;

    }

    @Override
    public Page<Post> recommendation(Set<String> tags, PaginationRequest paginationRequest) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(paginationRequest);

        // Most popular posts with matching tags
        Instant ninetyDaysAgo = Instant.now().minus(90, ChronoUnit.DAYS);

        Criteria criteria = Criteria.where("tags").in(tags)
                .and("isActive").is(true)
                .and("createdAt").gte(ninetyDaysAgo);

        Query query = new Query(criteria)
                .with(Sort.by(
                        Sort.Order.desc("stats.likeCount"),
                        Sort.Order.desc("stats.commentCount"),
                        Sort.Order.desc("stats.saveCount"),
                        Sort.Order.desc("createdAt")
                ))
                .with(pageable);

        List<Post> posts = mongoTemplate.find(query, Post.class);
        long total = mongoTemplate.count(query.limit(-1).skip(-1), Post.class);

        return new PageImpl<>(posts, pageable, total);
    }


    @Override
    public void updatePostForNewComment(String postId) {
        postRepository.updatePostForNewComment(postId);
        redisCacheService.deleteById(postId, Post.class);
    }

    @Override
    public void incrementLikeCount(String postId) {
        postRepository.incrementLikeCount(postId);
        redisCacheService.deleteById(postId, Post.class);
    }

    @Override
    public void decrementLikeCount(String postId) {
        postRepository.decrementLikeCount(postId);
        redisCacheService.deleteById(postId, Post.class);
    }


    @Override
    public void decrementSaveCount(String postId) {
        postRepository.decrementSaveCount(postId);
        redisCacheService.deleteById(postId, Post.class);
    }

    @Override
    public void incrementSaveCount(String postId) {
        postRepository.incrementSaveCount(postId);
        redisCacheService.deleteById(postId, Post.class);
    }


}
