package com.exposer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ExposerApiApplication {

    static void main(String[] args) {
        SpringApplication.run(ExposerApiApplication.class, args);
    }

}
