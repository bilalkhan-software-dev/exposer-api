package com.exposer.dao.interfaces;

import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.request.PostSearchRequest;
import com.exposer.models.entity.Post;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.Set;

public interface PostDao {

    Optional<Post> findById(String id);

    boolean existsById(String id);

    void deleteById(String id);

    Post save(Post t);

    Page<Post> findAll(PaginationRequest request);

    Page<Post> findByUser(String id, PaginationRequest request);

    Page<Post> search(PostSearchRequest searchRequest, PaginationRequest paginationRequest);

    Page<Post> recommendation(Set<String> tags, PaginationRequest paginationRequest);

    void updatePostForNewComment(String postId);

    void incrementLikeCount(String postId);

    void decrementLikeCount(String postId);

    void decrementSaveCount(String postId);

    void incrementSaveCount(String postId);
}
