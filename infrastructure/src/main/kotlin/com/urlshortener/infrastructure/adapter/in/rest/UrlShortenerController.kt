package com.urlshortener.infrastructure.adapter.`in`.rest

import com.urlshortener.domain.common.usecase.UseCaseHandler
import com.urlshortener.domain.model.UrlMapping
import com.urlshortener.domain.usecase.CreateShortUrl
import com.urlshortener.domain.usecase.ResolveShortUrl
import com.urlshortener.infrastructure.adapter.`in`.rest.dto.CreateShortUrlRequest
import com.urlshortener.infrastructure.adapter.`in`.rest.dto.ShortUrlResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/api")
@Tag(name = "URL Shortener", description = "URL shortening and resolution operations")
class UrlShortenerController(
    @Qualifier("createShortUrlUseCaseHandler")
    private val createShortUrlUseCaseHandler: UseCaseHandler<UrlMapping, CreateShortUrl>,

    @Qualifier("resolveShortUrlUseCaseHandler")
    private val resolveShortUrlUseCaseHandler: UseCaseHandler<UrlMapping, ResolveShortUrl>
) {
    @PostMapping
    @Operation(
        summary = "Create a shortened URL",
        description = "Creates a new shortened URL from the provided original URL"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "URL successfully shortened",
                content = [Content(schema = Schema(implementation = ShortUrlResponse::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid request data"
            )
        ]
    )
    fun createShortUrl(
        @Parameter(description = "Request containing the original URL to shorten")
        @RequestBody request: CreateShortUrlRequest
    ): ResponseEntity<ShortUrlResponse> {
        val saved = createShortUrlUseCaseHandler.handle(CreateShortUrl(request.originalUrl))
        val response = ShortUrlResponse(
            id = saved.id,
            originalUrl = saved.originalUrl,
            shortCode = saved.shortCode
        )
        return ResponseEntity.created(URI.create(saved.shortCode)).body(response)
    }

    @GetMapping("/{shortCode}")
    @Operation(
        summary = "Resolve a shortened URL",
        description = "Retrieves the original URL for a given short code"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "URL successfully resolved",
                content = [Content(schema = Schema(implementation = ShortUrlResponse::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Short code not found"
            )
        ]
    )
    fun resolveShortUrl(
        @Parameter(description = "The short code to resolve")
        @PathVariable shortCode: String
    ): ResponseEntity<ShortUrlResponse> {
        val urlMapping = resolveShortUrlUseCaseHandler.handle(ResolveShortUrl(shortCode))
        val response = ShortUrlResponse(
            id = urlMapping.id,
            originalUrl = urlMapping.originalUrl,
            shortCode = urlMapping.shortCode
        )
        return ResponseEntity.ok(response)
    }
}