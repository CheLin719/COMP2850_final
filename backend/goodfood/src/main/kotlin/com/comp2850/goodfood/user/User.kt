package com.comp2850.goodfood.user

data class User(
    val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: Role,
    val licence: String? = null,
    val proId: String? = null
)