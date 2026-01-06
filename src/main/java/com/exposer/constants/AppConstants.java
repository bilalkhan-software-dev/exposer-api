package com.exposer.constants;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AppConstants {

    private AppConstants() {}

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";
    private static final Long JWT_EXPIRATION_TIME = TimeUnit.DAYS.toMillis(1);

    public static final String ONLY_ADMIN = "hasRole('ADMIN')";
    public static final String DEFAULT_PAGE_SIZE = "20";
    public static final String DEFAULT_PAGE_NUM = "0";

    public static Date getJwtExpirationDate() {
        return new Date(System.currentTimeMillis() + JWT_EXPIRATION_TIME);
    }

}
