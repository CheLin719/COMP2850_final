package com.comp2850.goodfood.auth.controller

import com.comp2850.goodfood.auth.dto.LoginRequest
import com.comp2850.goodfood.auth.dto.RegisterRequest
import com.comp2850.goodfood.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): Map<String, Any?> {
        return authService.register(request)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): Map<String, Any?> {
        return authService.login(request)
    }

    @GetMapping("/users")
    fun getAllUsers(): List<Map<String, Any?>> {
        return authService.getAllUsers()
    }

    @GetMapping("/me")
    fun getCurrentUser(authentication: Authentication): Map<String, Any?> {
        return authService.getCurrentUser(authentication.name)
    }
}