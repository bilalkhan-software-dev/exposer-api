package com.exposer.models.dto.response;

import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PagedResponse<T> {

    private List<T> content;
    private long totalElements;
    private boolean isFirst;
    private boolean isLast;
    private int pageNumber;
    private int pageSize;
    private long totalPages;


}
