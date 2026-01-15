package com.exposer.dao.implementation;

import com.exposer.dao.interfaces.PostDao;
import com.exposer.dao.repository.PostRepository;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.request.PostSearchRequest;
import com.exposer.models.entity.Post;
import com.exposer.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
class PostDaoImpl implements PostDao {

    private final PostRepository postRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<Post> findById(String s) {
        return postRepository.findById(s);
    }

    @Override
    public boolean existsById(String s) {
        return postRepository.existsById(s);
    }

    @Override
    public void deleteById(String s) {
        postRepository.deleteById(s);
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Page<Post> findAll(PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        return postRepository.findAll(pageable);
    }

    @Override
    public Page<Post> findByUser(String userId, PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);
        return postRepository.findByAuthor_Id(userId, pageable);
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
    }

    @Override
    public void incrementLikeCount(String postId) {
        postRepository.incrementLikeCount(postId);
    }

    @Override
    public void decrementLikeCount(String postId) {
        postRepository.decrementLikeCount(postId);
    }


    @Override
    public void decrementSaveCount(String postId) {
        postRepository.decrementSaveCount(postId);
    }

    @Override
    public void incrementSaveCount(String postId) {
        postRepository.incrementSaveCount(postId);
    }


}
