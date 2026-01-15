package com.exposer.models.dto.response;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BasicUserResponse {

    private String id;
    private String username;
    private String fullName;
    private String profilePicture;

}
