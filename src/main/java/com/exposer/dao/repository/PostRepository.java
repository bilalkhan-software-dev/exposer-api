package com.exposer.dao.repository;

import com.exposer.models.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Set;


public interface PostRepository extends MongoRepository<Post, String> {

    Page<Post> findByAuthor_Id(String id, Pageable pageable);

    @Query(value = "{ 'title': { $regex: ?0, $options: 'i' } }",
            fields = "{ 'id': 1, 'title': 1, 'createdAt': 1, 'tags': 1, 'image': 1 }")
    Page<Post> findProjectedByTitle(String title, Pageable pageable);

    @Query(value = "{ 'tags': { $in: ?0 } }",
            fields = "{ 'id': 1, 'title': 1, 'createdAt': 1, 'tags': 1, 'image': 1 }")
    Page<Post> findProjectedByTags(Set<String> tags, Pageable pageable);

    @Query(value = "{ $and: [ "
            + "{ 'title': { $regex: ?0, $options: 'i' } }, "
            + "{ 'tags': { $in: ?1 } } "
            + "] }",
            fields = "{ 'id': 1, 'title': 1, 'createdAt': 1, 'tags': 1, 'image': 1 }")
    Page<Post> findProjectedByTitleAndTags(String title, Set<String> tags, Pageable pageable);


    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'postStats.likeCount': 1 } }")
    void incrementLikeCount(String postId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'postStats.likeCount': -1 } }")
    void decrementLikeCount(String postId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'hasComments': true }, '$inc': { 'postStats.commentCount': 1 } }")
    void updatePostForNewComment(String postId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'postStats.saveCount': 1 } }")
    void incrementSaveCount(String postId);

    @Query("{ '_id': ?0 }")
    @Update("{ '$inc': { 'postStats.saveCount': -1 } }")
    void decrementSaveCount(String postId);

}
