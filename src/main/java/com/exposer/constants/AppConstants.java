package com.exposer.constants;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class AppConstants {

    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    private static final Long JWT_EXPIRATION_TIME = TimeUnit.DAYS.toMillis(4);

    public static Date getJwtExpirationDate() {
        return new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME);
    }

    public static final String ONLY_ADMIN = "hasRole('ADMIN')";

    public static final Set<String> DEFAULT_TAGS = Set.of(
            "technical", "music", "education", "motivation", "java",
            "programming", "technology", "learning", "inspiration", "development"
    );
}
