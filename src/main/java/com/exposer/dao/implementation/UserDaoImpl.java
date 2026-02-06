package com.exposer.dao.implementation;

import com.exposer.dao.interfaces.RedisCacheService;
import com.exposer.dao.interfaces.UserDao;
import com.exposer.dao.repository.UserRepository;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.entity.User;
import com.exposer.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.time.Duration;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;
    private final RedisCacheService cacheService;

    @Override
    public Optional<User> findByUsernameOrEmail(String username) {

        return cacheService.getByName(username, User.class)
                .or(() -> userRepository.findByUsernameOrEmail(username)).map(user -> {
                    cacheService.putById(user.getId(), user, Duration.ofMinutes(5));
                    cacheService.putByName(user.getUsername(), user, Duration.ofMinutes(5));
                    return user;
                });
    }

    @Override
    public User save(User user) {
        User saved = userRepository.save(user);
        cacheService.putById(saved.getId(), saved, null);
        return saved;
    }

    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
        cacheService.deleteById(id, User.class);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existById(String id) {
        return userRepository.existsById(id);
    }

    /**
     * @param providerId is received from OAuthRequest
     */
    @Override
    public boolean existsByProviderId(String providerId) {
        return userRepository.existsByProviderId(providerId);
    }

    /**
     * @param providerId is received from OAuthRequest
     */
    @Override
    public Optional<User> findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId);
    }

    @Override
    public Optional<User> findById(String id) {

        return cacheService.getById(id, User.class)
                .or(() -> userRepository.findById(id))
                .map(user -> {
                    cacheService.putById(id, user, null);
                    return user;
                });

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {

        return cacheService.getByName(username, User.class)
                .or(() -> userRepository.findByUsername(username))
                .map(user -> {
                    cacheService.putByName(username, user, null);
                    return user;
                });
    }

    @Override
    public Optional<User> findByEmailAndVerificationToken(String email, String verificationToken) {
        return userRepository.findByEmailAndEmailVerificationToken(email, verificationToken);
    }

    @Override
    public Page<User> findByUsers(PaginationRequest request) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(request);

        return userRepository.findAll(pageable);

    }

    @Override
    public void deleteByUsername(String username) {
        userRepository.deleteByUsername(username);
        cacheService.deleteByName(username, User.class);
    }


}
