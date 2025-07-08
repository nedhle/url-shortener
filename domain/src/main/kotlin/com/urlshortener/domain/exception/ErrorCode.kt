package com.urlshortener.domain.exception

/**
 * Central place to store all domain-level error messages.
 * New errors should be added here so they stay consistent across the codebase.
 */
enum class ErrorCode(val message: String) {
    ORIGINAL_URL_BLANK("Original URL cannot be blank"),
    SHORT_CODE_BLANK("Short code cannot be blank"),
    ORIGINAL_URL_INVALID("Original URL is not valid"),
    SHORT_CODE_NOT_FOUND("Short code not found"),
    UNKNOWN("Unknown error");

    /**
     * Returns the default message or appends extra [details] if provided.
     */
    fun withDetails(details: String? = null): String =
        if (details.isNullOrBlank()) message else "$message: $details"
}
