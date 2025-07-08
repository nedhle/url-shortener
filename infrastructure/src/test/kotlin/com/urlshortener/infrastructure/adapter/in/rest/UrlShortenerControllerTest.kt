package com.urlshortener.infrastructure.adapter.`in`.rest

import com.urlshortener.domain.common.usecase.UseCaseHandler
import com.urlshortener.domain.model.UrlMapping
import com.urlshortener.domain.usecase.CreateShortUrl
import com.urlshortener.domain.usecase.ResolveShortUrl
import com.urlshortener.infrastructure.adapter.out.persistence.UrlMappingRepository
import com.urlshortener.infrastructure.adapter.out.redis.RedisCacheAdapter
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@WebMvcTest(controllers = [UrlShortenerController::class])
class UrlShortenerControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean(name = "createShortUrlUseCaseHandler")
    @Qualifier("createShortUrlUseCaseHandler")
    lateinit var createShortUrlUseCaseHandler: UseCaseHandler<UrlMapping, CreateShortUrl>

    @MockBean(name = "resolveShortUrlUseCaseHandler")
    @Qualifier("resolveShortUrlUseCaseHandler")
    lateinit var resolveShortUrlUseCaseHandler: UseCaseHandler<UrlMapping, ResolveShortUrl>

    @MockBean
    lateinit var urlMappingRepository: UrlMappingRepository

    @MockBean
    lateinit var redisCacheAdapter: RedisCacheAdapter


    @MockBean
    lateinit var redisTemplate: RedisTemplate<*, *>

    @Test
    fun `context loads and controller is present`() {
        assertNotNull(mockMvc)
    }

    @Test
    fun `should create short url`() {
        val urlMapping = UrlMapping(1L, "https://example.com", "short123")
        Mockito.`when`(createShortUrlUseCaseHandler.handle(CreateShortUrl("https://example.com"))).thenReturn(urlMapping)

        val requestBody = """{"originalUrl":"https://example.com"}"""

        mockMvc.perform(
            post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
            .andExpect(jsonPath("$.shortCode").value("short123"))
    }

    @Test
    fun `should resolve short url`() {
        val urlMapping = UrlMapping(1L, "https://example.com", "short123")
        Mockito.`when`(resolveShortUrlUseCaseHandler.handle(ResolveShortUrl("short123"))).thenReturn(urlMapping)

        mockMvc.perform(
            get("/api/short123")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.originalUrl").value("https://example.com"))
            .andExpect(jsonPath("$.shortCode").value("short123"))
    }

    @Test
    fun `should create short url with very long original url`() {
        val urlMapping = UrlMapping(1L, "https://www.example.com/search?q=aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "short123")
        Mockito.`when`(createShortUrlUseCaseHandler.handle(CreateShortUrl("https://example.com"))).thenReturn(urlMapping)

        val requestBody = """{"originalUrl":"https://example.com"}"""

        mockMvc.perform(
            post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.originalUrl").value("https://www.example.com/search?q=aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"))
            .andExpect(jsonPath("$.shortCode").value("short123"))
    }
} 