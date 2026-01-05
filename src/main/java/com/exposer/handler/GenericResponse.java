package com.exposer.handler;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;

import java.util.LinkedHashMap;
import java.util.Map;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenericResponse<T> {

    private HttpStatus httpStatus;
    private T data;
    private String message;
    private String status;

    @Builder.Default
    private Long timestamp = System.currentTimeMillis();

    public ResponseEntity<Map<String,Object>> create() {

        Map<String, Object> map = new LinkedHashMap<>();

        map.put("status", status);
        map.put("message", message);
        map.put("timestamp", timestamp);

        if (!ObjectUtils.isEmpty(data)) {
            map.put("data", data);
        }

        return new ResponseEntity<>(map, httpStatus);
    }


}
