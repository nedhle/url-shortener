package com.urlshortener.infrastructure

import com.urlshortener.domain.common.DomainComponent
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType

@SpringBootApplication
@ComponentScan(
    includeFilters = [ComponentScan.Filter(type = FilterType.ANNOTATION, value = [DomainComponent::class])]
)
class UrlShortenerApplication

fun main(args: Array<String>) {
    runApplication<UrlShortenerApplication>(*args)
} 