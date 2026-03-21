package com.comp2850.goodfood.auth.service

import com.comp2850.goodfood.auth.dto.LoginRequest
import com.comp2850.goodfood.auth.dto.RegisterRequest
import com.comp2850.goodfood.user.Role
import com.comp2850.goodfood.user.User
import com.comp2850.goodfood.user.repository.InMemoryUserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class AuthService(
    private val userRepository: InMemoryUserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(request: RegisterRequest): Map<String, Any> {
        if (request.name.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "name cannot be blank")
        }

        if (request.email.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "email cannot be blank")
        }

        if (request.password.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be blank")
        }

        val role = parseRole(request.role)

        val existingUser = userRepository.findByEmail(request.email)
        if (existingUser != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "email already exists")
        }

        val encodedPassword = passwordEncoder.encode(request.password)
            ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "password encoding failed")

        val user = User(
            id = UUID.randomUUID().toString(),
            name = request.name,
            email = request.email,
            password = encodedPassword,
            role = role
        )

        userRepository.save(user)

        return mapOf(
            "message" to "register success",
            "user" to mapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "role" to user.role.name
            )
        )
    }

    fun login(request: LoginRequest): Map<String, Any> {
        if (request.email.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "email cannot be blank")
        }

        if (request.password.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "password cannot be blank")
        }

        val user = userRepository.findByEmail(request.email)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid email or password")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid email or password")
        }

        return mapOf(
            "message" to "login success",
            "user" to mapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "role" to user.role.name
            )
        )
    }

    fun getAllUsers(): List<Map<String, Any>> {
        return userRepository.findAll().map { user ->
            mapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "role" to user.role.name
            )
        }
    }

    private fun parseRole(role: String): Role {
        return when (role.trim().uppercase()) {
            "SUBSCRIBER" -> Role.SUBSCRIBER
            "HEALTH_PROFESSIONAL" -> Role.HEALTH_PROFESSIONAL
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid role")
        }
    }
}