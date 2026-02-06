package com.exposer.utils;

import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.dto.response.PagedResponse;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.exposer.constants.AppConstants.DEFAULT_TAGS;

@UtilityClass
public class CommonUtil {

    public String getVerificationLink(String baseUrl, String email, String verificationToken) {
        return String.format("%s/api/v1/auth/verify-email?email=%s&token=%s",
                baseUrl, email, verificationToken);
    }

    public String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * @param <I>    the input type (source)
     * @param <O>    the output type (target)
     * @param page   the source page containing input elements
     * @param mapper function to convert input elements to output type
     * @return transformed paged response with output elements
     */
    public <I, O> PagedResponse<O> buildPagedResponse(Page<@NonNull I> page, Function<I, O> mapper) {
        return PagedResponse.<O>builder()
                .content(page.getContent().stream()
                        .map(mapper)
                        .toList())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    public Pageable toBuildSortAndPage(PaginationRequest request) {

        PaginationRequest paginationRequest = PaginationRequest.builder()
                .page(request.getPage())
                .size(request.getSize())
                .isNewest(request.getIsNewest())
                .sortBy(request.getSortBy())
                .build();

        Sort sort = Sort.by(Boolean.TRUE.equals(paginationRequest.getIsNewest()) ? Sort.Direction.DESC : Sort.Direction.ASC, paginationRequest.getSortBy());
        return PageRequest.of(paginationRequest.getPage(), paginationRequest.getSize(), sort);
    }

    public Set<String> parseCommaSeparatedTags(String tagsHeader) {
        if (tagsHeader == null || tagsHeader.trim().isEmpty()) {
            return DEFAULT_TAGS;
        }

        return Arrays.stream(tagsHeader.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }


}
