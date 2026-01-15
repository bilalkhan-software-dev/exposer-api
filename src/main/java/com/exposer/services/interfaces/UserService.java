package com.exposer.services.interfaces;

import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.request.ProfileUpdateRequest;
import com.exposer.models.dto.response.PagedResponse;
import com.exposer.models.dto.response.UserResponse;
import com.exposer.models.dto.response.admin.AdminUserResponse;

public interface UserService {

    UserResponse getProfile(String token);

    UserResponse getByUsername(String username);

    UserResponse updateProfile(String token, ProfileUpdateRequest profileUpdateRequest);

    void deleteUser(String username);

    PagedResponse<AdminUserResponse> getUsers(PaginationRequest request);

    UserResponse getById(String id);

}
