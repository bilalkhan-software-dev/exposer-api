package com.exposer.dao.interfaces;


import com.exposer.models.dto.request.PaginationRequest;
import tools.jackson.core.type.TypeReference;

import java.time.Duration;
import java.util.Optional;

public interface RedisCacheService {

    <T> void putById(String id, T value, Duration ttl);

    <T> void putByName(String name, T value, Duration ttl);

    <T> void putByPagination(String id, PaginationRequest request, String cachePrefix, T data, Duration ttl);

    <T> Optional<T> getById(String id, Class<T> clazz);

    <T> Optional<T> getByName(String id, Class<T> clazz);

    /**
     * Why we need TypeReference<T>
     * <p>
     * Problem:
     * <p>
     * Java type erasure removes generic type info at runtime.
     * <blockquote><pre>List<User>.class </pre></blockquote> does not exist, so ObjectMapper cannot infer the type from List.class.
     * <p>
     * Solution:
     * <p>
     * Use TypeReference<T> to preserve full generic type info.
     * <p>
     * Example:
     * <blockquote><pre>
     * List&lt;User&gt; users = objectMapper.readValue(json, new TypeReference&lt;List&lt;User&gt;&gt;() {});
     * </pre></blockquote><p>
     * <p>
     * ObjectMapper now knows it should deserialize JSON into a List of User objects.
     */

    <T> T getByPagination(String id, PaginationRequest request, String cachePrefix, TypeReference<T> typeRef);

    void deleteById(String id, Class<?> clazz);

    void deleteByName(String name, Class<?> clazz);

    /**
     * Why use versioning:
     *
     * @implNote <p>
     * Each entity or dataset has a single "version" key in Redis.
     * Pagination keys include the version (e.g., users:v5:page:0).
     * When underlying data changes, the version is incremented.
     * Old pages are automatically ignored; no manual deletion needed.
     * Ensures clients always get the latest data while old cache expires naturally.</p>
     *
     * Benefits:
     * <ol>
     *   <li>Simple and thread-safe (Redis INCR is atomic).</li>
     *   <li>Efficient cache invalidation without wildcards or scans.</li>
     *   <li>Scalable for large datasets and multiple users.</li>
     * </ol>
     */

    void incrementPaginationVersion(String id, String cachePrefix);
}
