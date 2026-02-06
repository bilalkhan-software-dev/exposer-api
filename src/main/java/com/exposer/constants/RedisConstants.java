package com.exposer.constants;

import java.time.Duration;

public final class RedisConstants {

    private RedisConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    public static final Duration REDIS_DEFAULT_TTL = Duration.ofHours(1);
    public static final Duration GLOBAL_REDIS_TTL = Duration.ofDays(1);

    public static final String USER_CACHE_PREFIX = "users:";
    public static final String POST_CACHE_PREFIX = "posts:";
    public static final String COMMENT_CACHE_PREFIX = "comments:";
    public static final String LIKE_CACHE_PREFIX = "likes:";
    public static final String SAVED_POST_CACHE_PREFIX = "saved-posts:";

}
