package com.exposer.services.interfaces;

import com.exposer.models.dto.request.ProfileUpdateRequest;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.UserResponse;

public interface UserService {

    UserResponse getProfile(String token);

    UserResponse getByUsername(String username);

    UserResponse updateProfile(String token, ProfileUpdateRequest profileUpdateRequest);

    void deleteUser(String username);

    PagedResponse<UserResponse> getUsers(int page, int size, boolean isNewest);

}
