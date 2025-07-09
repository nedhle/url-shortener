package com.urlshortener.infrastructure.adapter.out.persistence

import com.urlshortener.domain.model.UrlMapping
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UrlMappingPersistenceAdapterTest {

    private lateinit var repository: UrlMappingRepository
    private lateinit var adapter: UrlMappingPersistenceAdapter

    @BeforeEach
    fun setUp() {
        repository = mockk()
        adapter = UrlMappingPersistenceAdapter(repository)
    }

    @Test
    fun `should save UrlMapping successfully`() {
        // Given
        val urlMapping = UrlMapping(
            originalUrl = "https://www.example.com",
            shortCode = "abc123"
        )
        val entity = UrlMappingEntity(
            id = 0L,
            originalUrl = "https://www.example.com",
            shortCode = "abc123"
        )
        val savedEntity = entity.copy(id = 1L)
        
        every { repository.save(any()) } returns savedEntity

        // When
        val result = adapter.save(urlMapping)

        // Then
        assertEquals(1L, result.id)
        assertEquals(urlMapping.originalUrl, result.originalUrl)
        assertEquals(urlMapping.shortCode, result.shortCode)
        
        verify {
            repository.save(any())
        }
    }

    @Test
    fun `should find UrlMapping by short code successfully`() {
        // Given
        val shortCode = "abc123"
        val entity = UrlMappingEntity(
            id = 1L,
            originalUrl = "https://www.example.com",
            shortCode = shortCode
        )
        
        every { repository.findByShortCode(shortCode) } returns entity

        // When
        val result = adapter.findByShortCode(shortCode)

        // Then
        assertEquals(entity.id, result?.id)
        assertEquals(entity.originalUrl, result?.originalUrl)
        assertEquals(entity.shortCode, result?.shortCode)
        
        verify {
            repository.findByShortCode(shortCode)
        }
    }

    @Test
    fun `should return null when short code not found`() {
        // Given
        val shortCode = "nonexistent"
        
        every { repository.findByShortCode(shortCode) } returns null

        // When
        val result = adapter.findByShortCode(shortCode)

        // Then
        assertNull(result)
        verify {
            repository.findByShortCode(shortCode)
        }
    }

    @Test
    fun `should find UrlMapping by original URL successfully`() {
        // Given
        val originalUrl = "https://www.example.com"
        val entity = UrlMappingEntity(
            id = 1L,
            originalUrl = originalUrl,
            shortCode = "abc123"
        )
        
        every { repository.findByOriginalUrl(originalUrl) } returns entity

        // When
        val result = adapter.findByOriginalUrl(originalUrl)

        // Then
        assertEquals(entity.id, result?.id)
        assertEquals(entity.originalUrl, result?.originalUrl)
        assertEquals(entity.shortCode, result?.shortCode)
        
        verify {
            repository.findByOriginalUrl(originalUrl)
        }
    }

    @Test
    fun `should return null when original URL not found`() {
        // Given
        val originalUrl = "https://www.nonexistent.com"
        
        every { repository.findByOriginalUrl(originalUrl) } returns null

        // When
        val result = adapter.findByOriginalUrl(originalUrl)

        // Then
        assertNull(result)
        verify {
            repository.findByOriginalUrl(originalUrl)
        }
    }

    @Test
    fun `should handle empty short code in findByShortCode`() {
        // Given
        val emptyShortCode = ""
        
        every { repository.findByShortCode(emptyShortCode) } returns null

        // When
        val result = adapter.findByShortCode(emptyShortCode)

        // Then
        assertNull(result)
        verify {
            repository.findByShortCode(emptyShortCode)
        }
    }

    @Test
    fun `should handle empty original URL in findByOriginalUrl`() {
        // Given
        val emptyUrl = ""
        
        every { repository.findByOriginalUrl(emptyUrl) } returns null

        // When
        val result = adapter.findByOriginalUrl(emptyUrl)

        // Then
        assertNull(result)
        verify {
            repository.findByOriginalUrl(emptyUrl)
        }
    }
} 