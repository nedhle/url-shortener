package com.urlshortener.infrastructure.adapter.out.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.urlshortener.domain.model.UrlMapping
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration
import org.mockito.Mockito.`when` as mockWhen
import com.urlshortener.infrastructure.config.CacheProperties

class RedisCacheAdapterTest {

    private lateinit var redisTemplate: RedisTemplate<String, String>
    private lateinit var valueOperations: ValueOperations<String, String>
    private lateinit var objectMapper: ObjectMapper
    private lateinit var adapter: RedisCacheAdapter

    @BeforeEach
    fun setUp() {
        // Create mocks
        valueOperations = mockk(relaxed = true)
        redisTemplate = mockk(relaxed = true) {
            every { opsForValue() } returns valueOperations
        }
        objectMapper = jacksonObjectMapper()
        val cacheProperties = CacheProperties(ttlMinutes = 2)
        adapter = RedisCacheAdapter(redisTemplate, objectMapper, cacheProperties)
    }

    @Test
    fun `should put UrlMapping into cache successfully`() {
        // Given
        val shortCode = "abc123"
        val urlMapping = UrlMapping(
            id = 1L,
            originalUrl = "https://www.example.com",
            shortCode = shortCode
        )

        // When
        adapter.put(shortCode, urlMapping)

        // Then
        verify(exactly = 1) {
            valueOperations.set(
                eq(shortCode),
                any<String>(),
                any<Duration>()
            )
        }
    }

    @Test
    fun `should return null when key not found in cache`() {
        // Given
        val shortCode = "nonexistent"
        every { valueOperations.get(shortCode) } returns null

        // When
        val result = adapter.get(shortCode)

        // Then
        assertNull(result)
        verify(exactly = 1) { valueOperations.get(shortCode) }
    }

    @Test
    fun `should return UrlMapping when found in cache`() {
        // Given
        val shortCode = "abc123"
        val urlMapping = UrlMapping(
            id = 1L,
            originalUrl = "https://www.example.com",
            shortCode = shortCode
        )
        val json = objectMapper.writeValueAsString(urlMapping)
        
        every { valueOperations.get(shortCode) } returns json

        // When
        val result = adapter.get(shortCode)

        // Then
        assertNotNull(result)
        assertEquals(urlMapping.originalUrl, result?.originalUrl)
        assertEquals(urlMapping.shortCode, result?.shortCode)
        verify(exactly = 1) { valueOperations.get(shortCode) }
    }

    @Test
    fun `should return null when JSON deserialization fails`() {
        // Given
        val shortCode = "abc123"
        val invalidJson = "{invalid json}"
        every { valueOperations.get(shortCode) } returns invalidJson

        // When
        val result = adapter.get(shortCode)

        // Then
        assertNull(result)
        verify(exactly = 1) { valueOperations.get(shortCode) }
    }

    @Test
    fun `should handle serialization exception gracefully`() {
        // Given
        val shortCode = "abc123"
        val urlMapping = UrlMapping(
            id = 1L,
            originalUrl = "https://www.example.com",
            shortCode = shortCode
        )

        every { 
            valueOperations.set(
                eq(shortCode),
                any<String>(),
                any<Duration>()
            ) 
        } throws RuntimeException("Redis error")

        // When & Then - Should not throw exception
        assertDoesNotThrow {
            adapter.put(shortCode, urlMapping)
        }
    }

    @Test
    fun `should handle empty short code`() {
        // Given
        val shortCode = ""
        
        // When - Test with empty string directly in the adapter methods
        assertDoesNotThrow {
            // Test that put with empty string doesn't throw and doesn't interact with Redis
            adapter.put(shortCode, 
                UrlMapping(
                    id = 1L,
                    originalUrl = "https://www.example.com",
                    shortCode = "valid" // Use a valid short code for the object
                )
            )
            
            // Test that get with empty string doesn't throw and doesn't interact with Redis
            val result = adapter.get(shortCode)
            
            // Then - Should return null for empty short code
            assertNull(result)
        }
        
        // Verify no interactions with Redis operations
        verify(exactly = 0) { 
            valueOperations.set(
                any(),
                any<String>(),
                any<Duration>()
            )
        }
        verify(exactly = 0) { valueOperations.get(any()) }
    }
} 