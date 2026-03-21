package com.comp2850.goodfood.auth.dto

data class LoginRequest(
    val email: String,
    val password: String
)