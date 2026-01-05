package com.exposer.models.dto.response;


import com.exposer.models.entity.enums.AccountStatus;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthResponse {

    private String username;
    private String token;
    private AccountStatus accountStatus;

}
