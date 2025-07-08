package com.urlshortener.domain.model

import com.urlshortener.domain.exception.InvalidInputException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UrlMappingTest {

    @Test
    fun `should create UrlMapping with valid data`() {
        // Given
        val id = 1L
        val originalUrl = "https://www.example.com"
        val shortCode = "abc123"

        // When
        val urlMapping = UrlMapping(id, originalUrl, shortCode)

        // Then
        assertEquals(id, urlMapping.id)
        assertEquals(originalUrl, urlMapping.originalUrl)
        assertEquals(shortCode, urlMapping.shortCode)
    }

    @Test
    fun `should create UrlMapping with default id`() {
        // Given
        val originalUrl = "https://www.example.com"
        val shortCode = "abc123"

        // When
        val urlMapping = UrlMapping(originalUrl = originalUrl, shortCode = shortCode)

        // Then
        assertEquals(0L, urlMapping.id)
        assertEquals(originalUrl, urlMapping.originalUrl)
        assertEquals(shortCode, urlMapping.shortCode)
    }

    @Test
    fun `should throw exception when originalUrl is blank`() {
        // Given & When & Then
        assertThrows<InvalidInputException> {
            UrlMapping(originalUrl = "", shortCode = "abc123")
        }

        assertThrows<InvalidInputException> {
            UrlMapping(originalUrl = "   ", shortCode = "abc123")
        }
    }

    @Test
    fun `should throw exception when shortCode is blank`() {
        // Given & When & Then
        assertThrows<InvalidInputException> {
            UrlMapping(originalUrl = "https://www.example.com", shortCode = "")
        }

        assertThrows<InvalidInputException> {
            UrlMapping(originalUrl = "https://www.example.com", shortCode = "   ")
        }
    }

    @Test
    fun `should handle URLs with special characters`() {
        val specialUrl = "https://www.example.com/path?query=param&another=one"
        val shortCode = "special1"

        val urlMapping = UrlMapping(originalUrl = specialUrl, shortCode = shortCode)

        assertEquals(specialUrl, urlMapping.originalUrl)
        assertEquals(shortCode, urlMapping.shortCode)
    }

    @Test
    fun `should handle very long URLs`() {
        // Given
        val originalUrl = "https://www.example.com/" + "a".repeat(1000)
        val shortCode = "abc123"

        // When
        val urlMapping = UrlMapping(originalUrl = originalUrl, shortCode = shortCode)

        // Then
        assertEquals(originalUrl, urlMapping.originalUrl)
        assertEquals(shortCode, urlMapping.shortCode)
    }

    @Test
    fun `should handle short codes with special characters`() {
        // Given
        val originalUrl = "https://www.example.com"
        val shortCode = "abc-123_456"

        // When
        val urlMapping = UrlMapping(originalUrl = originalUrl, shortCode = shortCode)

        // Then
        assertEquals(originalUrl, urlMapping.originalUrl)
        assertEquals(shortCode, urlMapping.shortCode)
    }

    @Test
    fun `should be equal when all properties are the same`() {
        // Given
        val urlMapping1 = UrlMapping(1L, "https://www.example.com", "abc123")
        val urlMapping2 = UrlMapping(1L, "https://www.example.com", "abc123")

        // When & Then
        assertEquals(urlMapping1, urlMapping2)
        assertEquals(urlMapping1.hashCode(), urlMapping2.hashCode())
    }

    @Test
    fun `should not be equal when properties are different`() {
        // Given
        val urlMapping1 = UrlMapping(1L, "https://www.example.com", "abc123")
        val urlMapping2 = UrlMapping(2L, "https://www.example.com", "abc123")
        val urlMapping3 = UrlMapping(1L, "https://www.example2.com", "abc123")
        val urlMapping4 = UrlMapping(1L, "https://www.example.com", "def456")

        // When & Then
        assertNotNull(urlMapping1)
        assertNotNull(urlMapping2)
        assertNotNull(urlMapping3)
        assertNotNull(urlMapping4)
        
        // They should not be equal
        assert(urlMapping1 != urlMapping2)
        assert(urlMapping1 != urlMapping3)
        assert(urlMapping1 != urlMapping4)
    }
} 