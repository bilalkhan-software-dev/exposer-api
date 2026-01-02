package com.exposer.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class GenericResponseHandler {

    public ResponseEntity<?> createBuildResponse(String message, Object data, HttpStatus httpStatusCode) {
        GenericResponse response = GenericResponse.builder()
                .httpStatus(httpStatusCode)
                .status("success")
                .message(message)
                .data(data)
                .build();
        return response.create();
    }

    public ResponseEntity<?> createBuildResponseMessage(String message, HttpStatus httpStatusCode) {
        GenericResponse response = GenericResponse.builder()
                .httpStatus(httpStatusCode)
                .status("success")
                .message(message)
                .build();
        return response.create();
    }

    public ResponseEntity<?> createErrorResponse(String message, Object data, HttpStatus httpStatusCode) {
        GenericResponse response = GenericResponse.builder()
                .httpStatus(httpStatusCode)
                .status("error")
                .message(message)
                .data(data)
                .build();
        return response.create();

    }

    public ResponseEntity<?> createErrorResponseMessage(String message, HttpStatus httpStatusCode) {
        GenericResponse response = GenericResponse.builder()
                .httpStatus(httpStatusCode)
                .status("error")
                .message(message)
                .build();
        return response.create();
    }


}
