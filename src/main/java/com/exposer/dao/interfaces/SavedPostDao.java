package com.exposer.dao.interfaces;

import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.entity.SavedPost;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface SavedPostDao {
    Optional<SavedPost> findById(String id);

    boolean existsById(String id);

    void deleteById(String id);

    SavedPost save(SavedPost savedPost);

    Page<SavedPost> findAll(PaginationRequest request);

    Page<SavedPost> findByUser(String userId, PaginationRequest request);

    boolean existsByUserAndPostId(String userId, String postId);

}
