package com.urlshortener.infrastructure.config

import com.urlshortener.domain.CreateShortUrlUseCaseHandler
import com.urlshortener.domain.ResolveShortUrlUseCaseHandler
import com.urlshortener.domain.common.usecase.UseCaseHandler
import com.urlshortener.domain.model.UrlMapping
import com.urlshortener.domain.port.CachePort
import com.urlshortener.domain.port.UrlMappingPort
import com.urlshortener.domain.usecase.CreateShortUrl
import com.urlshortener.domain.usecase.ResolveShortUrl
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCaseHandlerConfig {
    @Bean
    @Qualifier("createShortUrlUseCaseHandler")
    fun createShortUrlHandler(
        urlMappingPort: UrlMappingPort,
        cachePort: CachePort
    ): UseCaseHandler<UrlMapping, CreateShortUrl> =
        CreateShortUrlUseCaseHandler(urlMappingPort, cachePort)

    @Bean
    @Qualifier("resolveShortUrlUseCaseHandler")
    fun resolveShortUrlHandler(
        urlMappingPort: UrlMappingPort,
        cachePort: CachePort
    ): UseCaseHandler<UrlMapping, ResolveShortUrl> =
        ResolveShortUrlUseCaseHandler(urlMappingPort, cachePort)
} 