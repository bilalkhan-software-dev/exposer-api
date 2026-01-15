package com.exposer.dao.interfaces;

import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.entity.Like;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface LikeDao {

    Optional<Like> findById(String id);

    boolean existsById(String id);

    boolean existsByUserIdAndTargetId(String userId, String targetId);

    void deleteById(String id);

    Like save(Like like);

    Page<Like> findAll(PaginationRequest request);

    Page<Like> findByTargetId(String targetId, PaginationRequest request);

    Page<Like> findByUserId(String userId, PaginationRequest request);
}
