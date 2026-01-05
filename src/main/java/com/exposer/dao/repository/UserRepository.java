package com.exposer.dao.repository;

import com.exposer.models.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailVerificationToken(String verifiedToken);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByProviderId(String providerId);

    Optional<User> findByProviderId(String providerId);

    Optional<User> findByEmailAndEmailVerificationToken(String email, String verifiedToken);


}
