package com.urlshortener.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "cache")
data class CacheProperties(
    var ttlMinutes: Long = 2
)