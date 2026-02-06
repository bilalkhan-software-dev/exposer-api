package com.exposer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ExposerApiApplication {

    static void main(String[] args) {
        SpringApplication.run(ExposerApiApplication.class, args);
    }

}
