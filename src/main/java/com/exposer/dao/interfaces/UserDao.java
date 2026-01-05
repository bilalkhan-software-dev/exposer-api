package com.exposer.dao.interfaces;

import com.exposer.models.entity.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByUsernameOrEmail(String username);

    User save(User user);

    void deleteById(String id);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existById(String id);

    boolean existsByProviderId(String providerId);

    Optional<User> findByProviderId(String providerId);

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailAndVerificationToken(String email, String verificationToken);

    Page<User> findByUsers(int page, int size, boolean isNewest);
}
