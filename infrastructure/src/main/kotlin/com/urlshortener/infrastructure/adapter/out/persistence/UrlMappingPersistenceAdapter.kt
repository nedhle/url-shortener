package com.urlshortener.infrastructure.adapter.out.persistence

import com.urlshortener.domain.model.UrlMapping
import com.urlshortener.domain.port.UrlMappingPort
import org.springframework.stereotype.Service

@Service
class UrlMappingPersistenceAdapter(
    private val repository: UrlMappingRepository
) : UrlMappingPort {
    override fun save(urlMapping: UrlMapping): UrlMapping {
        val entity = with(UrlMappingMapper) { urlMapping.toEntity() }
        return with(UrlMappingMapper) { repository.save(entity).toDomain() }
    }

    override fun findByShortCode(shortCode: String): UrlMapping? {
        return with(UrlMappingMapper) { repository.findByShortCode(shortCode)?.toDomain() }
    }

    override fun findByOriginalUrl(originalUrl: String): UrlMapping? {
        return with(UrlMappingMapper) { repository.findByOriginalUrl(originalUrl)?.toDomain() }
    }
}