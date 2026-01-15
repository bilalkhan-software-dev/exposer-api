package com.exposer.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavedPostRequest {

    @NotBlank(message = "Post ID is required.")
    private String postId;

    private String notes;

}
