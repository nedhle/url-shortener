package com.urlshortener.infrastructure.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UrlMappingRepository : JpaRepository<UrlMappingEntity, Long> {
    fun findByShortCode(shortCode: String): UrlMappingEntity?
    fun findByOriginalUrl(originalUrl: String): UrlMappingEntity?
}