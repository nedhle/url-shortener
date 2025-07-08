package com.urlshortener.domain

import com.urlshortener.domain.exception.InvalidInputException
import com.urlshortener.domain.model.UrlMapping
import com.urlshortener.domain.port.CachePort
import com.urlshortener.domain.port.UrlMappingPort
import com.urlshortener.domain.usecase.CreateShortUrl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.mockito.Mockito.`when` as mockWhen

class CreateShortUrlUseCaseHandlerTest {

    private lateinit var urlMappingPort: UrlMappingPort
    private lateinit var cachePort: CachePort
    private lateinit var handler: CreateShortUrlUseCaseHandler

    @BeforeEach
    fun setUp() {
        urlMappingPort = mock(UrlMappingPort::class.java)
        cachePort = mock(CachePort::class.java)
        handler = CreateShortUrlUseCaseHandler(urlMappingPort, cachePort)
    }

    @Test
    fun `should create new URL mapping when URL does not exist`() {
        val originalUrl = "https://www.example.com/very/long/url"
        val useCase = CreateShortUrl(originalUrl)

        whenever(urlMappingPort.findByOriginalUrl(originalUrl)).thenReturn(null)

        val captor = argumentCaptor<UrlMapping>()
        whenever(urlMappingPort.save(captor.capture())).thenAnswer {
            captor.firstValue.copy(id = 1L)
        }

        val result = handler.handle(useCase)

        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals(originalUrl, result.originalUrl)
        assertEquals(8, result.shortCode.length)

        verify(urlMappingPort).findByOriginalUrl(originalUrl)
        verify(urlMappingPort).save(any())
        verify(cachePort).put(result.shortCode, result)

        val savedUrlMapping = captor.firstValue
        assertEquals(originalUrl, savedUrlMapping.originalUrl)
    }
    @Test
    fun `should return existing URL mapping when URL already exists`() {
        val originalUrl = "https://www.example.com/existing"
        val existingMapping = UrlMapping(
            id = 1L,
            originalUrl = originalUrl,
            shortCode = "abc123"
        )
        val useCase = CreateShortUrl(originalUrl)

        mockWhen(urlMappingPort.findByOriginalUrl(originalUrl)).thenReturn(existingMapping)

        val result = handler.handle(useCase)

        assertEquals(existingMapping, result)

        verify(urlMappingPort).findByOriginalUrl(originalUrl)
        verify(urlMappingPort, times(0)).save(any())
        verify(cachePort).put(existingMapping.shortCode, existingMapping)
    }

    @Test
    fun `should cache the created URL mapping`() {
        val originalUrl = "https://www.example.com/new/url"
        val useCase = CreateShortUrl(originalUrl)

        mockWhen(urlMappingPort.findByOriginalUrl(originalUrl)).thenReturn(null)

        val captor = argumentCaptor<UrlMapping>()
        whenever(urlMappingPort.save(captor.capture())).thenAnswer {
            captor.firstValue.copy(id = 1L)
        }

        val result = handler.handle(useCase)

        verify(cachePort).put(result.shortCode, result)
    }

    @Test
    fun `should throw exception when URL is blank`() {
        val blankUrl = "   "
        val useCase = CreateShortUrl(blankUrl)

        val exception = assertThrows<InvalidInputException> {
            handler.handle(useCase)
        }
        assertEquals("Original URL cannot be blank", exception.message)
        verifyNoInteractions(urlMappingPort, cachePort)
    }

    @Test
    fun `should handle empty URL gracefully`() {
        val emptyUrl = ""
        val useCase = CreateShortUrl(emptyUrl)

        val exception = assertThrows<InvalidInputException> {
            handler.handle(useCase)
        }
        assertEquals("Original URL cannot be blank", exception.message)
        verifyNoInteractions(urlMappingPort, cachePort)
    }
}
