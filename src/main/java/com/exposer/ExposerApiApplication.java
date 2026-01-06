package com.exposer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableCaching
@EnableAspectJAutoProxy
public class ExposerApiApplication {

    static void main(String[] args) {
        SpringApplication.run(ExposerApiApplication.class, args);
    }

}
