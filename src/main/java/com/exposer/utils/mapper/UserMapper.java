package com.exposer.utils.mapper;

import com.exposer.models.dto.response.UserResponse;
import com.exposer.models.entity.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static UserResponse toUserResponse(User user) {
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

}
