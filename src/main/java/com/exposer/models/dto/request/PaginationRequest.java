package com.exposer.models.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Schema(description = "Pagination request parameters")
public class PaginationRequest {

    @Builder.Default
    private int page = 0;

    @Max(value = 100, message = "Page size cannot exceed 100")
    @Builder.Default
    private int size = 20;

    @Builder.Default
    private boolean isNewest = true;

    @Builder.Default
    private String sortBy = "createdAt";

}
