package com.urlshortener.domain.port

import com.urlshortener.domain.model.UrlMapping

interface CachePort {
    fun get(shortCode: String): UrlMapping?
    fun put(shortCode: String, urlMapping: UrlMapping)
}