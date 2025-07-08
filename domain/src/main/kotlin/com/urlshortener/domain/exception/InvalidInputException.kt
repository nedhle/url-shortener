package com.urlshortener.domain.exception

/**
 * Thrown when an input value violates domain constraints.
 * Always reference a value from [ErrorCode] to keep messages consistent.
 */
class InvalidInputException(
    val errorCode: ErrorCode,
    details: String? = null
) : RuntimeException(errorCode.withDetails(details))