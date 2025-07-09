package com.urlshortener.infrastructure.adapter.out.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.urlshortener.domain.model.UrlMapping
import com.urlshortener.domain.port.CachePort
import com.urlshortener.infrastructure.config.CacheProperties
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisCacheAdapter(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    cacheProperties: CacheProperties
) : CachePort {

    private val logger = LoggerFactory.getLogger(RedisCacheAdapter::class.java)

    private val ttl: Duration = Duration.ofMinutes(cacheProperties.ttlMinutes)
    
    override fun get(shortCode: String): UrlMapping? {
        if (shortCode.isBlank()) {
            logger.warn("Attempted to get with empty or blank shortCode")
            return null
        }
        return try {
            val json = redisTemplate.opsForValue().get(shortCode) ?: return null
            objectMapper.readValue(json, UrlMapping::class.java)
        } catch (e: Exception) {
            logger.warn("Failed to deserialize UrlMapping from cache for shortCode: {}", shortCode, e)
            null
        }
    }

    override fun put(shortCode: String, urlMapping: UrlMapping) {
        if (shortCode.isBlank()) {
            logger.warn("Attempted to put with empty or blank shortCode")
            return
        }
        try {
            val json = objectMapper.writeValueAsString(urlMapping)
            redisTemplate.opsForValue().set(shortCode, json, ttl)
            logger.debug("Cached UrlMapping: {} -> {}", shortCode, urlMapping.originalUrl)
        } catch (e: Exception) {
            logger.error("Failed to serialize UrlMapping to cache for shortCode: {}", shortCode, e)
        }
    }
}