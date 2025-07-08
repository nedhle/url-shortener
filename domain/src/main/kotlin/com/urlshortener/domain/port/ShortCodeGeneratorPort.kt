package com.urlshortener.domain.port

interface ShortCodeGeneratorPort {
    fun generate(): String
}