package com.urlshortener.infrastructure.adapter.`in`.rest.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

@Schema(description = "Request to create a shortened URL")
data class CreateShortUrlRequest(
    @field:NotBlank(message = "Original URL is required")
    @field:Pattern(
        regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})[/\\w .-]*/?$",
        message = "Please provide a valid URL"
    )
    @field:Schema(
        description = "The original URL to be shortened",
        example = "https://www.example.com/very/long/url/path"
    )
    val originalUrl: String
)