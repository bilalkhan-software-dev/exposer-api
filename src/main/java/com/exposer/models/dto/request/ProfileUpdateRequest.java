package com.exposer.models.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProfileUpdateRequest {


    private String fullName;
    private String profilePic;


}
