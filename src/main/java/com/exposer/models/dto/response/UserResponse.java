package com.exposer.models.dto.response;


import com.exposer.models.entity.enums.AccountStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
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
