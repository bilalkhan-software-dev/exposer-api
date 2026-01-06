package com.exposer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class MongoConfig implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) return Optional.of("system");


        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return Optional.of((userDetails.getUsername()));
        }

        return Optional.of("system");
    }
}


