package com.exposer.dao.repository;

import com.exposer.models.entity.Like;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface LikeRepository extends MongoRepository<Like, String> {

    Page<Like> findByTargetId(String targetId, Pageable pageable);


    boolean existsByUserIdAndTargetId(String userId, String targetId);

    Page<Like> findByUserId(String userId, Pageable pageable);
}
