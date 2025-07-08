package com.urlshortener.domain

import com.urlshortener.domain.common.DomainComponent
import com.urlshortener.domain.common.usecase.UseCaseHandler
import com.urlshortener.domain.model.UrlMapping
import com.urlshortener.domain.port.CachePort
import com.urlshortener.domain.port.UrlMappingPort
import com.urlshortener.domain.usecase.CreateShortUrl
import org.slf4j.LoggerFactory
import java.util.*

@DomainComponent
class CreateShortUrlUseCaseHandler(
    private val urlMappingPort: UrlMappingPort,
    private val cachePort: CachePort
) : UseCaseHandler<UrlMapping, CreateShortUrl> {

    private val logger = LoggerFactory.getLogger(CreateShortUrlUseCaseHandler::class.java)

    override fun handle(useCase: CreateShortUrl): UrlMapping {
        val urlMapping = createNewMapping(useCase.originalUrl)

        urlMappingPort.findByOriginalUrl(useCase.originalUrl)?.let { existingMapping ->
            logger.info("Found existing URL mapping: {} -> {}", existingMapping.shortCode, existingMapping.originalUrl)

            safeCachePut(existingMapping.shortCode, existingMapping)
            return existingMapping
        }

        val savedUrlMapping = urlMappingPort.save(urlMapping)
        logger.info("Created new URL mapping: {} -> {}", savedUrlMapping.shortCode, savedUrlMapping.originalUrl)

        safeCachePut(savedUrlMapping.shortCode, savedUrlMapping)

        return savedUrlMapping
    }

    /**
     * Attempts to put the given UrlMapping into the cache, suppressing any exceptions that may occur.
     *
     * While the current CachePort implementation (e.g., RedisCacheAdapter) handles its own exceptions,
     * this method provides an additional safety layer and serves as a centralized hook point for future
     * enhancements such as retry policies, circuit breakers, or metrics/logging instrumentation.
     *
     * This defensive design ensures that cache failures do not impact the main application flow.
     */
    private fun safeCachePut(shortCode: String, urlMapping: UrlMapping) {
        try {
            cachePort.put(shortCode, urlMapping)
            logger.debug("Successfully cached URL mapping for shortCode: {}", shortCode)
        } catch (e: Exception) {
            logger.warn("Failed to cache URL mapping for shortCode: {} - {}", shortCode, e.message)
        }
    }

    private fun createNewMapping(originalUrl: String): UrlMapping {
        val shortCode = generateUniqueShortCode()
        return UrlMapping(
            originalUrl = originalUrl,
            shortCode = shortCode
        )
    }

    /**
     * Generates a short code using the first 8 characters of a UUID.
     *
     * Note: This is a simplified strategy suitable for MVP or low-scale systems (e.g. < 100,000 entries).
     * Although UUIDs offer a high degree of randomness, using only the first 8 characters introduces a minor risk of collision.
     *
     * TODO: Replace this with a scalable, collision-free ID generator like Snowflake + Base62 encoding
     * for production environments or when expecting large-scale usage.
     */
    private fun generateUniqueShortCode(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }
}