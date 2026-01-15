package com.exposer.models.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditPostRequest {

    @Size(min = 3, max = 200, message = "Title must be between 3 and 200 characters")
    private String title;

    @Size(min = 10, max = 5000, message = "Content must be between 10 and 5000 characters")
    private String content;

    private String thumbnail;

    private Set<String> tags;

    private Boolean isActive;


}
