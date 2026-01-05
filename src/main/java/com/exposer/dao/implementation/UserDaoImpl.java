package com.exposer.dao.implementation;

import com.exposer.dao.interfaces.UserDao;
import com.exposer.dao.repository.UserRepository;
import com.exposer.models.entity.User;
import com.exposer.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserDaoImpl implements UserDao {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByUsernameOrEmail(String username) {
        return userRepository.findByUsernameOrEmail(username, username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(String id) {
        userRepository.deleteById(id);
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

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return userRepository.findByProviderId(providerId);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmailAndVerificationToken(String email, String verificationToken) {
        return userRepository.findByEmailAndEmailVerificationToken(email, verificationToken);
    }

    @Override
    public Page<User> findByUsers(int page, int size, boolean isNewest) {

        Pageable pageable = CommonUtil.toBuildSortAndPage(page, size, isNewest);

        return userRepository.findAll(pageable);

    }


}
