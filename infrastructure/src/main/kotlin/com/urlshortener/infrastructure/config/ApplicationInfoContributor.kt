package com.urlshortener.infrastructure.config

import org.springframework.boot.actuate.info.Info
import org.springframework.boot.actuate.info.InfoContributor
import org.springframework.stereotype.Component

@Component
class ApplicationInfoContributor : InfoContributor {

    override fun contribute(builder: Info.Builder) {
        builder.withDetail("application", mapOf(
            "name" to "URL Shortener",
            "description" to "A service for creating and resolving shortened URLs",
            "version" to "1.0.0",
            "features" to listOf(
                "URL shortening",
                "URL resolution",
                "Redis caching",
                "PostgreSQL persistence"
            )
        ))
        
        builder.withDetail("technology", mapOf(
            "framework" to "Spring Boot",
            "language" to "Kotlin",
            "database" to "PostgreSQL",
            "cache" to "Redis",
            "architecture" to "Hexagonal Architecture"
        ))
    }
} 