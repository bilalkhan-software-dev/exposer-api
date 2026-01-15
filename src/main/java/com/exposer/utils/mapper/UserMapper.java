package com.exposer.utils.mapper;

import com.exposer.models.dto.response.BasicUserResponse;
import com.exposer.models.dto.response.admin.AdminUserResponse;
import com.exposer.models.dto.response.UserResponse;
import com.exposer.models.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public UserResponse toUserResponse(User user) {

        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .profilePic(user.getProfilePic())
                .accountStatus(user.getAccountStatus())
                .emailVerifiedAt(user.getEmailVerifiedAt())
                .providerType(user.getProviderType())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public BasicUserResponse toBasicUserResponse(User user) {

        if (user == null) {
            return BasicUserResponse.builder()
                    .id("unknown")
                    .username("Unknown User")
                    .fullName("Unknown User")
                    .profilePicture(null)
                    .build();
        }

        return BasicUserResponse.builder()
                .username(user.getUsername())
                .id(user.getId())
                .profilePicture(user.getProfilePic())
                .fullName(user.getFullName())
                .build();
    }

    public AdminUserResponse toAdminUserResponse(User user) {

        if (user == null) {
            return null;
        }

        return AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .profilePic(user.getProfilePic())
                .accountStatus(user.getAccountStatus())
                .emailVerifiedAt(user.getEmailVerifiedAt())
                .providerType(user.getProviderType())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }


}
