package com.exposer.models.dto.response;


import com.exposer.models.entity.enums.AccountStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class UserResponse {

    private String username;
    private String fullName;
    private String email;
    private String profilePic;

    private String providerType;

    private AccountStatus accountStatus;
    private Instant emailVerifiedAt;

    private Instant createdAt;
    private Instant updatedAt;


}
