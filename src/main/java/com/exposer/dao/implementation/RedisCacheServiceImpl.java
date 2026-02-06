package com.exposer.dao.implementation;

import com.exposer.dao.interfaces.RedisCacheService;
import com.exposer.models.dto.request.PaginationRequest;
import com.exposer.models.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.Optional;

import static com.exposer.constants.RedisConstants.*;


@Service
@Slf4j
@RequiredArgsConstructor
class RedisCacheServiceImpl implements RedisCacheService {


    private final RedisTemplate<String, String> redisTemplate;
    private final JsonMapper jsonMapper;

    // Hash field names
    private static final String BY_ID_FIELD = "BY_ID";
    private static final String BY_NAME_FIELD = "BY_NAME";
    private static final String VERSION_KEY = "%s%s:version";
    private static final String CACHE_PAGINATION_FORMAT = "%s%s:v%d:page:%d:size:%d:sortBy:%s:isNewest:%b";

    @Override
    public <T> void putById(final String id, final T value, Duration ttl) {
        String cacheKey = getCacheKey(id, value.getClass());
        put(cacheKey, BY_ID_FIELD, value, ttl);
    }

    @Override
    public <T> void putByName(final String name, final T value, Duration ttl) {
        String cacheKey = getCacheKey(name, value.getClass());
        put(cacheKey, BY_NAME_FIELD, value, ttl);
    }

    @Override
    public <T> void putByPagination(final String id, final PaginationRequest request, final String cachePrefix, T data, Duration ttl) {
        String cacheKey = getPaginationCacheKey(id, request, cachePrefix);

        try {

            Duration expiry = (ttl != null && !ttl.isNegative() && !ttl.isZero())
                    ? ttl : REDIS_DEFAULT_TTL;

            String json = jsonMapper.writeValueAsString(data);
            redisTemplate.opsForValue().set(cacheKey, json, expiry);

        } catch (JacksonException e) {
            log.error("Error when put pagination cached data: {}", e.getMessage());
        }

    }

    @Override
    public <T> Optional<T> getById(final String id, final Class<T> clazz) {
        String cacheKey = getCacheKey(id, clazz);
        return get(cacheKey, BY_ID_FIELD, clazz);
    }

    @Override
    public <T> Optional<T> getByName(final String name, final Class<T> clazz) {
        String cacheKey = getCacheKey(name, clazz);
        return get(cacheKey, BY_NAME_FIELD, clazz);
    }


    @Override
    public <T> T getByPagination(final String id, final PaginationRequest request, final String cachePrefix, TypeReference<T> typeRef) {

        String cacheKey = getPaginationCacheKey(id, request, cachePrefix);


        String stringJson = redisTemplate.opsForValue().get(cacheKey);

        if (stringJson == null) return null;

        try {
            return jsonMapper.readValue(stringJson, typeRef);
        } catch (JacksonException e) {
            log.error("Error when retrieving caching pagination data: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteById(final String id, final Class<?> clazz) {
        String cacheKey = getCacheKey(id, clazz);
        redisTemplate.delete(cacheKey);
    }

    @Override
    public void deleteByName(final String name, final Class<?> clazz) {
        String cacheKey = getCacheKey(name, clazz);
        redisTemplate.delete(cacheKey);
    }

    @Override
    public void incrementPaginationVersion(String id, String cachePrefix) {
        String versionKey = String.format(VERSION_KEY, cachePrefix, id);
        redisTemplate.opsForValue().increment(versionKey);
    }

    private <T> Optional<T> get(final String cacheKey, final String hashField, final Class<T> clazz) {

        try {
            HashOperations<String, String, String> ops = redisTemplate.opsForHash();
            String json = ops.get(cacheKey, hashField);

            if (json == null || json.isEmpty()) {
                return Optional.empty();
            }

            T value = jsonMapper.readValue(json, clazz);
            return Optional.ofNullable(value);
        } catch (JacksonException e) {
            log.error("Error when retrieve cached data: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private <T> void put(final String cacheKey, final String hashField, final T data, final Duration ttl) {

        try {

            Duration expiry = (ttl != null && !ttl.isNegative() && !ttl.isZero())
                    ? ttl : REDIS_DEFAULT_TTL;

            String json = jsonMapper.writeValueAsString(data);
            HashOperations<String, String, String> ops = redisTemplate.opsForHash();
            ops.put(cacheKey, hashField, json);

            redisTemplate.expire(cacheKey, expiry);

        } catch (JacksonException e) {
            log.error("Error when store cached data: {}", e.getMessage());
        }
    }

    /**
     * @param identifier unique(ObjectId) id or username
     */
    private String getCacheKey(final String identifier, final Class<?> clazz) {

        if (User.class.isAssignableFrom(clazz)) {
            return USER_CACHE_PREFIX.concat(identifier);
        } else if (Post.class.isAssignableFrom(clazz)) {
            return POST_CACHE_PREFIX.concat(identifier);
        } else if (Comment.class.isAssignableFrom(clazz)) {
            return COMMENT_CACHE_PREFIX.concat(identifier);
        } else if (SavedPost.class.isAssignableFrom(clazz)) {
            return SAVED_POST_CACHE_PREFIX.concat(identifier);
        } else if (Like.class.isAssignableFrom(clazz)) {
            return LIKE_CACHE_PREFIX.concat(identifier);
        } else {
            // Fallback to generic key or throw exception
            log.error("Unsupported class: {}", clazz.getName());
            throw new IllegalArgumentException("Unsupported class type: " + clazz.getName());
        }
    }

    private String getPaginationCacheKey(final String id, PaginationRequest request, final String cachePrefix) {

        PaginationRequest paginationRequest = PaginationRequest.builder()
                .page(request.getPage())
                .size(request.getSize())
                .isNewest(request.getIsNewest())
                .sortBy(request.getSortBy())
                .build();

        long version = getVersion(id, cachePrefix);


        return String.format(CACHE_PAGINATION_FORMAT,
                cachePrefix, id, version, paginationRequest.getPage(),
                paginationRequest.getSize(),
                paginationRequest.getSortBy(),
                paginationRequest.getIsNewest());
    }


    private long getVersion(String id, String cachePrefix) {
        String versionKey = String.format(VERSION_KEY, cachePrefix, id);

        String version = redisTemplate.opsForValue().get(versionKey);

        if (version == null) {
            redisTemplate.opsForValue().set(versionKey, "1");
            return 1L;
        }

        return Long.parseLong(version);
    }


}


