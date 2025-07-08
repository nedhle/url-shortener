package com.urlshortener.domain.common.usecase

import com.urlshortener.domain.common.model.UseCase

interface UseCaseHandler<R, T : UseCase> {
    fun handle(useCase: T): R
}