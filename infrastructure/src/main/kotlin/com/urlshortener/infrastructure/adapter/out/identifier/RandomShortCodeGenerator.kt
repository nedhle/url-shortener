package com.urlshortener.infrastructure.adapter.out.identifier

import com.urlshortener.domain.port.ShortCodeGeneratorPort
import org.springframework.stereotype.Service

/**
 * RandomShortCodeGenerator generates an 8-character Base62-encoded short code using UUID.
 *
 * TODO: For production or horizontally scalable systems, consider replacing this
 * with a distributed ID generator like Twitter Snowflake, Redis-based counter,
 * or a central ID service to avoid collisions and ensure ordering across multiple nodes.
 */
@Service
class RandomShortCodeGenerator : ShortCodeGeneratorPort {
    override fun generate(): String {
        TODO("Not yet implemented")
    }

}