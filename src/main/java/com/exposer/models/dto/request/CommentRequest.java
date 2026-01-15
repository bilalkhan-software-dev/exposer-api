package com.exposer.models.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Comment description is required")
    @Size(max = 200, message = "Comment is too long. Only 200 character allowed")
    private String description;

    @NotBlank(message = "Post ID is required")
    @Size(min = 7, message = "Post ID is too short")
    private String postId;


}
