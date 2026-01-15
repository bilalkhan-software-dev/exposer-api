package com.exposer.models.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Search request parameters")
public class PostSearchRequest {

    @NotBlank(message = "Query is required")
    @Size(max = 60, message = "Query is too long")
    private String query;

    @Size(max = 10, message = "Maximum 10 tags allowed")
    private Set<String> tags;

}
