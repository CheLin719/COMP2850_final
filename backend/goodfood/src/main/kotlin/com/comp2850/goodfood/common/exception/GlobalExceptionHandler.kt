package com.comp2850.goodfood.common.exception

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(
        ex: ResponseStatusException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now().toString(),
            "status" to ex.statusCode.value(),
            "error" to ex.statusCode.toString(),
            "message" to ex.reason,
            "path" to request.requestURI
        )

        return ResponseEntity.status(ex.statusCode).body(body)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now().toString(),
            "status" to 500,
            "error" to "Internal Server Error",
            "message" to ex.message,
            "path" to request.requestURI
        )

        return ResponseEntity.internalServerError().body(body)
    }
}