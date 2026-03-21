package com.comp2850.goodfood.auth.dto

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)