package com.exposer.models.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditCommentRequest {

    @Size(max = 200, message = "Comment is too long. Only 200 character allowed")
    private String description;

}
