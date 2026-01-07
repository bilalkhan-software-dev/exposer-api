package com.exposer.utils;

import com.exposer.models.dto.response.PagedResponse;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.UUID;
import java.util.function.Function;

@UtilityClass
public class CommonUtil {

    public static String getVerificationLink(String baseUrl, String email, String verificationToken) {
        return baseUrl + "/api/v1/auth/verify-email?email=" + email + "&token=" + verificationToken;
    }

    public static String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * @param <I>    the input type (source)
     * @param <O>    the output type (target)
     * @param page   the source page containing input elements
     * @param mapper function to convert input elements to output type
     * @return transformed paged response with output elements
     */
    public static <I, O> PagedResponse<O> buildPagedResponse(Page<@NonNull I> page, Function<I, O> mapper) {
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

    public static Pageable toBuildSortAndPage(final int page, final int size, final boolean isNewest) {
        Sort sort = Sort.by(isNewest ? Sort.Direction.DESC : Sort.Direction.ASC, "createdAt");
        return PageRequest.of(page, size, sort);
    }


}
