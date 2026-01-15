package com.exposer.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePostStatusRequest {

    @NotBlank(message = "Post ID is required")
    private String postId;

    @NotNull(message = "Status is required")
    private Boolean status;

}
