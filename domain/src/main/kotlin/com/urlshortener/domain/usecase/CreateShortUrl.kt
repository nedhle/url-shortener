package com.urlshortener.domain.usecase

import com.urlshortener.domain.common.model.UseCase

data class CreateShortUrl(
    val originalUrl: String
) : UseCase