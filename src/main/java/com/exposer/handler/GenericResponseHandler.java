package com.exposer.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

import static com.exposer.constants.ErrorMessage.STATUS_ERROR;
import static com.exposer.constants.ErrorMessage.STATUS_SUCCESS;

@UtilityClass
public class GenericResponseHandler {

    public <T> ResponseEntity<Map<String, Object>> createBuildResponse(String message, T data, HttpStatus httpStatusCode) {
        GenericResponse<T> response = GenericResponse.<T>builder()
                .httpStatus(httpStatusCode)
                .status(STATUS_SUCCESS)
                .message(message)
                .data(data)
                .build();
        return response.create();
    }

    public <T> ResponseEntity<Map<String, Object>> createBuildResponseMessage(String message, HttpStatus httpStatusCode) {
        GenericResponse<T> response = GenericResponse.<T>builder()
                .httpStatus(httpStatusCode)
                .status(STATUS_SUCCESS)
                .message(message)
                .build();
        return response.create();
    }

    public <T> ResponseEntity<Map<String, Object>> createErrorResponse(String message, T data, HttpStatus httpStatusCode) {
        GenericResponse<T> response = GenericResponse.<T>builder()
                .httpStatus(httpStatusCode)
                .status(STATUS_ERROR)
                .message(message)
                .data(data)
                .build();
        return response.create();

    }

    public <T> ResponseEntity<Map<String, Object>> createErrorResponseMessage(String message, HttpStatus httpStatusCode) {
        GenericResponse<T> response = GenericResponse.<T>builder()
                .httpStatus(httpStatusCode)
                .status(STATUS_ERROR)
                .message(message)
                .build();
        return response.create();
    }

    public void jwtResponse(HttpServletResponse response, Exception e) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Object body = GenericResponse.builder()
                .status(STATUS_ERROR)
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message(e.getMessage())
                .build().create().getBody();

        response.getWriter().write(new ObjectMapper().writeValueAsString(body));

    }


}
