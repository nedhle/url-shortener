package com.urlshortener.domain.port

import com.urlshortener.domain.model.UrlMapping

interface UrlMappingPort {
    fun save(urlMapping: UrlMapping): UrlMapping
    fun findByShortCode(shortCode: String): UrlMapping?
    fun findByOriginalUrl(originalUrl: String): UrlMapping?
}