package com.urlshortener.infrastructure.config

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisHealthIndicator(
    private val redisTemplate: RedisTemplate<String, String>
) : HealthIndicator {

    override fun health(): Health {
        return try {
            redisTemplate.opsForValue().get("health-check")
            
            Health.up()
                .withDetail("redis", "Available")
                .withDetail("host", "localhost")
                .withDetail("port", 6379)
                .build()
        } catch (e: Exception) {
            Health.down()
                .withDetail("redis", "Unavailable")
                .withDetail("error", e.message)
                .build()
        }
    }
} 