package com.comp2850.goodfood.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RegisterRequest(
    @field:NotBlank(message = "name cannot be blank")
    val name: String,

    @field:NotBlank(message = "email cannot be blank")
    @field:Email(message = "email format is invalid")
    val email: String,

    @field:NotBlank(message = "password cannot be blank")
    val password: String,

    @field:NotBlank(message = "role cannot be blank")
    val role: String,

    val licence: String? = null
)