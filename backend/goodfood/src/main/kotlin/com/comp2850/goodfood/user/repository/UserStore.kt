package com.comp2850.goodfood.user.repository

import com.comp2850.goodfood.user.User

interface UserStore {
    fun save(user: User): User
    fun findByEmail(email: String): User?
    fun findAll(): List<User>
}