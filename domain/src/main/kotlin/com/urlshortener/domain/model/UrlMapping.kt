package com.urlshortener.domain.model

import com.urlshortener.domain.exception.ErrorCode
import com.urlshortener.domain.exception.InvalidInputException
import java.net.MalformedURLException
import java.net.URL

/**
 * Domain model representing a shortened URL
 * @property id Unique identifier for the shortened URL
 * @property originalUrl The original URL that was shortened
 * @property shortCode The unique code used in the shortened URL
 */
data class UrlMapping(
    val id: Long = 0,
    val originalUrl: String,
    val shortCode: String,
) {
    init {
        if (originalUrl.isBlank()) throw InvalidInputException(ErrorCode.ORIGINAL_URL_BLANK)
        if (shortCode.isBlank()) throw InvalidInputException(ErrorCode.SHORT_CODE_BLANK)
        if (!validateUrl(originalUrl)) throw InvalidInputException(ErrorCode.ORIGINAL_URL_INVALID)
    }

    companion object {
        private const val BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

        fun validateUrl(url: String): Boolean {
            if (url.isBlank()) return false

            val urlRegex = "^(https?://)[\\w.-]+(\\.[a-zA-Z]{2,})(:\\d+)?(/[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]*)?$".toRegex()

            if (!urlRegex.matches(url)) return false
            return try {
                val normalized = url.takeIf { it.startsWith("http://") || it.startsWith("https://") }
                    ?: "https://$url"
                URL(normalized)
                true
            } catch (e: MalformedURLException) {
                false
            }
        }
    }
}