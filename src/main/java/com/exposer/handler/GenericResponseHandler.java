package com.exposer.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;


@UtilityClass
public class GenericResponseHandler {

    public static <T> ResponseEntity<Map<String, Object>> createBuildResponse(String message, T data, HttpStatus httpStatusCode) {
        GenericResponse<T> response = GenericResponse.<T>builder()
                .httpStatus(httpStatusCode)
                .status("success")
                .message(message)
                .data(data)
                .build();
        return response.create();
    }

    public static <T> ResponseEntity<Map<String, Object>> createBuildResponseMessage(String message, HttpStatus httpStatusCode) {
        GenericResponse<T> response = GenericResponse.<T>builder()
                .httpStatus(httpStatusCode)
                .status("success")
                .message(message)
                .build();
        return response.create();
    }

    public static <T> ResponseEntity<Map<String, Object>> createErrorResponse(String message, T data, HttpStatus httpStatusCode) {
        GenericResponse<T> response = GenericResponse.<T>builder()
                .httpStatus(httpStatusCode)
                .status("error")
                .message(message)
                .data(data)
                .build();
        return response.create();

    }

    public static <T> ResponseEntity<Map<String, Object>> createErrorResponseMessage(String message, HttpStatus httpStatusCode) {
        GenericResponse<T> response = GenericResponse.<T>builder()
                .httpStatus(httpStatusCode)
                .status("error")
                .message(message)
                .build();
        return response.create();
    }

    public static void jwtResponse(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Object body = GenericResponse.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(e.getMessage())
                .build().create().getBody();

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));

    }


}
