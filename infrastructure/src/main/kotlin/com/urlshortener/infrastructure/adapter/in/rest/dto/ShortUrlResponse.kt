package com.urlshortener.infrastructure.adapter.`in`.rest.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Response containing the shortened URL information")
data class ShortUrlResponse(
    @field:Schema(description = "Unique identifier for the URL mapping", example = "1")
    val id: Long,
    
    @field:Schema(description = "The original URL that was shortened", example = "https://www.example.com/very/long/url/path")
    val originalUrl: String,
    
    @field:Schema(description = "The short code for the URL", example = "abc123")
    val shortCode: String
) 