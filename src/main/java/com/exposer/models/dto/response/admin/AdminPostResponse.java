package com.exposer.models.dto.response.admin;

import com.exposer.models.dto.response.PostResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AdminPostResponse extends PostResponse {

    private String createdBy;
    private String updatedBy;

}
