package com.urlshortener.domain.usecase

import com.urlshortener.domain.common.model.UseCase

data class ResolveShortUrl(
    val shortCode: String
) : UseCase