package com.urlshortener.infrastructure.adapter.out.persistence

import com.urlshortener.domain.model.UrlMapping

object UrlMappingMapper {
    fun UrlMapping.toEntity() = UrlMappingEntity(
        id = id,
        originalUrl = originalUrl,
        shortCode = shortCode
    )

    fun UrlMappingEntity.toDomain() = UrlMapping(
        id = id,
        originalUrl = originalUrl,
        shortCode = shortCode
    )
} 