package com.exposer.models.dto.response.admin;

import com.exposer.models.dto.response.UserResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminUserResponse extends UserResponse {

    private String id;

    private String createdBy;
    private String updatedBy;

}
