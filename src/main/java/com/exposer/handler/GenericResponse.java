package com.exposer.handler;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashMap;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericResponse {

    private HttpStatus httpStatus;
    private Object data;
    private String message;
    private String status;

    public ResponseEntity<?> create() {

        LinkedHashMap<String, Object> map = new LinkedHashMap<>();

        map.put("status", status);
        map.put("message", message);

        if (!ObjectUtils.isEmpty(data)) {
            map.put("data", data);
        }

        return new ResponseEntity<>(map, httpStatus);
    }


}
