package com.exposer.models.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import static com.exposer.constants.ErrorMessage.STATUS_ERROR;
import static com.exposer.constants.ErrorMessage.STATUS_SUCCESS;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * status : success / error
     */
    private String status;

    private String message;
    private T data;

    /**
     * httpStatus : Http Status Code i.e: 404, 401, 200, 201 etc.
     */
    private HttpStatus httpStatus;


    public static <T> ApiResponse<T> success(String message, T data, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .status(STATUS_SUCCESS)
                .httpStatus(httpStatus)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .message(message)
                .status(STATUS_SUCCESS)
                .httpStatus(httpStatus)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .message(message)
                .status(STATUS_ERROR)
                .httpStatus(httpStatus)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, T data, HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .status(STATUS_ERROR)
                .httpStatus(httpStatus)
                .build();
    }

}
