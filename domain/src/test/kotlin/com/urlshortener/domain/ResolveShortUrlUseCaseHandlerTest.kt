package com.urlshortener.domain

import com.urlshortener.domain.exception.UrlNotFoundException
import com.urlshortener.domain.model.UrlMapping
import com.urlshortener.domain.port.CachePort
import com.urlshortener.domain.port.UrlMappingPort
import com.urlshortener.domain.usecase.ResolveShortUrl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.mockito.Mockito.`when` as mockWhen

class ResolveShortUrlUseCaseHandlerTest {

    private lateinit var urlMappingPort: UrlMappingPort
    private lateinit var cachePort: CachePort
    private lateinit var handler: ResolveShortUrlUseCaseHandler

    @BeforeEach
    fun setUp() {
        urlMappingPort = mock(UrlMappingPort::class.java)
        cachePort = mock(CachePort::class.java)
        handler = ResolveShortUrlUseCaseHandler(urlMappingPort, cachePort)
    }

    @Test
    fun `should return URL mapping from cache when available`() {
        // Given
        val shortCode = "abc123"
        val cachedMapping = UrlMapping(
            id = 1L,
            originalUrl = "https://www.example.com",
            shortCode = shortCode
        )
        val useCase = ResolveShortUrl(shortCode)
        
        mockWhen(cachePort.get(shortCode)).thenReturn(cachedMapping)

        // When
        val result = handler.handle(useCase)

        // Then
        assertEquals(cachedMapping, result)
        
        verify(cachePort, times(1)).get(shortCode)
        
        verify(urlMappingPort, never()).findByShortCode(anyString())
    }

    @Test
    fun `should return URL mapping from database when not in cache`() {
        // Given
        val shortCode = "abc123"
        val dbMapping = UrlMapping(
            id = 1L,
            originalUrl = "https://www.example.com",
            shortCode = shortCode
        )
        val useCase = ResolveShortUrl(shortCode)
        
        mockWhen(cachePort.get(shortCode)).thenReturn(null)
        mockWhen(urlMappingPort.findByShortCode(any())).thenReturn(dbMapping)

        // When
        val result = handler.handle(useCase)

        // Then
        assertEquals(dbMapping, result)
        
        verify(cachePort, times(1)).get(shortCode)
        verify(urlMappingPort, times(1)).findByShortCode(any())
        verify(cachePort, times(1)).put(shortCode, dbMapping)
    }

    @Test
    fun `should throw UrlNotFoundException when short code not found in cache or database`() {
        // Given
        val shortCode = "invalid123"
        val useCase = ResolveShortUrl(shortCode)
        
        mockWhen(cachePort.get(shortCode)).thenReturn(null)
        mockWhen(urlMappingPort.findByShortCode(shortCode)).thenReturn(null)

        // When & Then
        val exception = assertThrows<UrlNotFoundException> {
            handler.handle(useCase)
        }
        
        assertEquals("Short code not found: $shortCode", exception.message)
        
        verify(cachePort).get(shortCode)
        verify(urlMappingPort).findByShortCode(shortCode)
        verifyNoMoreInteractions(cachePort, urlMappingPort)
    }

    @Test
    fun `should handle null cache gracefully`() {
        // Given
        val shortCode = "abc123"
        val dbMapping = UrlMapping(
            id = 1L,
            originalUrl = "https://www.example.com",
            shortCode = shortCode
        )
        val useCase = ResolveShortUrl(shortCode)
        
        mockWhen(cachePort.get(shortCode)).thenReturn(null)
        mockWhen(urlMappingPort.findByShortCode(any())).thenReturn(dbMapping)

        // When
        val result = handler.handle(useCase)

        // Then
        assertNotNull(result)
        assertEquals(dbMapping, result)
    }

    @Test
    fun `should cache database result for future lookups`() {
        // Given
        val shortCode = "abc123"
        val dbMapping = UrlMapping(
            id = 1L,
            originalUrl = "https://www.example.com",
            shortCode = shortCode
        )
        val useCase = ResolveShortUrl(shortCode)
        
        mockWhen(cachePort.get(shortCode)).thenReturn(null)
        mockWhen(urlMappingPort.findByShortCode(any())).thenReturn(dbMapping)

        // When
        handler.handle(useCase)

        // Then
        verify(cachePort, times(1)).put(shortCode, dbMapping)
    }
} 