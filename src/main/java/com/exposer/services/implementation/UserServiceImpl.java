package com.exposer.services.implementation;

import com.exposer.dao.interfaces.UserDao;
import com.exposer.exception.ResourceNotFoundException;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.request.ProfileUpdateRequest;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.UserResponse;
import com.exposer.models.dto.response.admin.AdminUserResponse;
import com.exposer.models.entity.User;
import com.exposer.services.interfaces.UserService;
import com.exposer.utils.AuthUtils;
import com.exposer.utils.CommonUtil;
import com.exposer.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final AuthUtils authUtils;


    @Override
    public UserResponse getProfile(String token) {
        log.info("Fetching user profile");
        User user = authUtils.getUserFromToken(token);

        log.info("Profile fetched successfully");
        return UserMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getByUsername(String username) {
        log.info("Fetching user details by username");
        User user = userDao.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("No user found with username: " + username)
        );

        log.info("User details By Username fetched successfully");
        return UserMapper.toUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponse updateProfile(String token, ProfileUpdateRequest request) {
        log.info("Updating user profile");
        User user = authUtils.getUserFromToken(token);

        Optional.ofNullable(request.getFullName()).ifPresent(user::setFullName);
        Optional.ofNullable(request.getProfilePic()).ifPresent(user::setProfilePic);

        if (request.getProfilePic() != null || request.getFullName() != null) {
            log.info("Updating in database");
            userDao.save(user);
        }

        log.info("User details updated successfully");
        return UserMapper.toUserResponse(user);
    }

    @Override
    public void deleteUser(String username) {
        log.info("Deleting user details by username");
        boolean exists = userDao.existsByUsername(username);
        if (!exists) {
            log.info("User not found with username: {}", username);
            throw new ResourceNotFoundException("No user found with username: " + username);
        }
        userDao.deleteByUsername(username);
        log.info("User details deleted successfully");
    }

    @Override
    public PagedResponse<AdminUserResponse> getUsers(PaginationRequest request) {
        log.info("Fetching all users");
        Page<User> users = userDao.findByUsers(request);

        log.info(users.getTotalElements() == 0 ? "No users available in our record" : "Users fetched successfully");
        return CommonUtil.buildPagedResponse(users, UserMapper::toAdminUserResponse);
    }

    @Override
    public UserResponse getById(String id) {
        log.info("Fetching user details by id");
        User user = userDao.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("No user found")
        );

        log.info("User details by ID fetched successfully");
        return UserMapper.toUserResponse(user);
    }


}
