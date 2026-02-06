package com.exposer.handler;

import com.exposer.models.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@UtilityClass
public class ResponseHandler {

    public <T> ResponseEntity<ApiResponse<T>> createBuildResponse(String message, T data, HttpStatus httpStatusCode) {
        ApiResponse<T> response = ApiResponse.success(message, data, httpStatusCode);
        return new ResponseEntity<>(response, httpStatusCode);
    }

    public <T> ResponseEntity<ApiResponse<T>> createBuildResponseMessage(String message, HttpStatus httpStatusCode) {
        ApiResponse<T> response = ApiResponse.success(message, httpStatusCode);
        return new ResponseEntity<>(response, httpStatusCode);
    }

    public <T> ResponseEntity<ApiResponse<T>> createErrorResponse(String message, T data, HttpStatus httpStatusCode) {
        ApiResponse<T> response = ApiResponse.error(message, data, httpStatusCode);
        return new ResponseEntity<>(response, httpStatusCode);
    }

    public <T> ResponseEntity<ApiResponse<T>> createErrorResponseMessage(String message, HttpStatus httpStatusCode) {
        ApiResponse<T> response = ApiResponse.error(message, httpStatusCode);
        return new ResponseEntity<>(response, httpStatusCode);
    }


    public void jwtResponse(HttpServletResponse response, Exception e) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ApiResponse<Object> error = ApiResponse.error(e.getMessage(), HttpStatus.UNAUTHORIZED);
        new ObjectMapper().writeValue(response.getOutputStream(), error);

    }

}
