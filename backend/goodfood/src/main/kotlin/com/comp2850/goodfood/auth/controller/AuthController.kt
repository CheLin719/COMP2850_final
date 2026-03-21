package com.comp2850.goodfood.auth.controller

import com.comp2850.goodfood.auth.dto.LoginRequest
import com.comp2850.goodfood.auth.dto.RegisterRequest
import com.comp2850.goodfood.auth.service.AuthService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): Map<String, Any> {
        return authService.register(request)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): Map<String, Any> {
        return authService.login(request)
    }

    @GetMapping("/users")
    fun getAllUsers(): List<Map<String, Any>> {
        return authService.getAllUsers()
    }
}