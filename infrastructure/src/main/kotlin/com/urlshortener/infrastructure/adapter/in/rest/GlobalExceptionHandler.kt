package com.urlshortener.infrastructure.adapter.`in`.rest

import com.urlshortener.domain.exception.InvalidInputException
import com.urlshortener.domain.exception.UrlNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    data class ErrorResponse(
        val timestamp: LocalDateTime = LocalDateTime.now(),
        val status: Int,
        val error: String,
        val message: String
    )

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.NOT_FOUND
        val errorResponse = ErrorResponse(
            status = status.value(),
            error = status.name,
            message = ex.message ?: "The requested resource was not found"
        )
        return ResponseEntity(errorResponse, status)
    }

    @ExceptionHandler(UrlNotFoundException::class)
    fun handleUrlNotFoundException(ex: UrlNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.NOT_FOUND
        val errorResponse = ErrorResponse(
            status = status.value(),
            error = status.name,
            message = ex.message.toString()
        )
        return ResponseEntity(errorResponse, status)
    }

    @ExceptionHandler(InvalidInputException::class)
    fun handleUrlNotValidException(ex: InvalidInputException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.BAD_REQUEST
        val errorResponse = ErrorResponse(
            status = status.value(),
            error = status.name,
            message = ex.message.toString()
        )
        return ResponseEntity(errorResponse, status)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val errorResponse = ErrorResponse(
            status = status.value(),
            error = status.name,
            message = ex.message ?: "An unexpected error occurred"
        )
        return ResponseEntity(errorResponse, status)
    }
}