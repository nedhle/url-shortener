package com.urlshortener.domain.exception

/**
 * Thrown when a requested URL mapping cannot be located.
 */
class UrlNotFoundException(
    val errorCode: ErrorCode = ErrorCode.SHORT_CODE_NOT_FOUND,
    details: String? = null
) : RuntimeException(errorCode.withDetails(details))