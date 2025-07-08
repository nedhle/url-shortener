package com.urlshortener.domain

import com.urlshortener.domain.common.DomainComponent
import com.urlshortener.domain.common.usecase.UseCaseHandler
import com.urlshortener.domain.exception.ErrorCode
import com.urlshortener.domain.exception.UrlNotFoundException
import com.urlshortener.domain.model.UrlMapping
import com.urlshortener.domain.port.CachePort
import com.urlshortener.domain.port.UrlMappingPort
import com.urlshortener.domain.usecase.ResolveShortUrl
import org.slf4j.LoggerFactory

@DomainComponent
class ResolveShortUrlUseCaseHandler(
    private val urlMappingPort: UrlMappingPort,
    private val cachePort: CachePort
) : UseCaseHandler<UrlMapping, ResolveShortUrl> {

    private val logger = LoggerFactory.getLogger(ResolveShortUrlUseCaseHandler::class.java)

    override fun handle(useCase: ResolveShortUrl): UrlMapping {
        cachePort.get(useCase.shortCode)?.let { urlMapping ->
            logger.info("Cache HIT: {} -> {}", useCase.shortCode, urlMapping.originalUrl)
            return urlMapping
        }

        val urlMapping = urlMappingPort.findByShortCode(useCase.shortCode)
            ?: run {
                logger.info("URL not found: {}", useCase.shortCode)
                throw UrlNotFoundException(ErrorCode.SHORT_CODE_NOT_FOUND, useCase.shortCode)
            }

        cachePort.put(urlMapping.shortCode, urlMapping)
        logger.info("Cache MISS, found in DB: {} -> {}", useCase.shortCode, urlMapping.originalUrl)

        return urlMapping
    }
}