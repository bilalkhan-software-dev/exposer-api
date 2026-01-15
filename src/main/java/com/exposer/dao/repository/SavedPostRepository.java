package com.exposer.dao.repository;

import com.exposer.models.entity.SavedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SavedPostRepository extends MongoRepository<SavedPost, String> {


    boolean existsByUserIdAndPostId(String userId, String postId);


    Page<SavedPost> findByUserId(String userId, Pageable pageable);

}
