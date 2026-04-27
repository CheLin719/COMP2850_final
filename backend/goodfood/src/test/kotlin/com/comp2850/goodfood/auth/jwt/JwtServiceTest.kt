package com.comp2850.goodfood.auth.jwt

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JwtServiceTest {

    private lateinit var jwtService: JwtService
    private val secret = "TestSecretKeyThatIsLongEnoughForHS384Signing12345"
    private val expiration = 86400000L

    @BeforeEach
    fun setUp() {
        jwtService = JwtService(secret, expiration)
    }

    @Test
    fun `generateToken returns non-blank token`() {
        val token = jwtService.generateToken("user@test.com", "SUBSCRIBER")
        assertTrue(token.isNotBlank())
    }

    @Test
    fun `generateToken for subscriber has correct role`() {
        val token = jwtService.generateToken("user@test.com", "SUBSCRIBER")
        assertEquals("SUBSCRIBER", jwtService.extractRole(token))
    }

    @Test
    fun `generateToken for professional has correct role`() {
        val token = jwtService.generateToken("pro@test.com", "HEALTH_PROFESSIONAL")
        assertEquals("HEALTH_PROFESSIONAL", jwtService.extractRole(token))
    }

    @Test
    fun `extractEmail returns correct email`() {
        val email = "user@example.com"
        val token = jwtService.generateToken(email, "SUBSCRIBER")
        assertEquals(email, jwtService.extractEmail(token))
    }

    @Test
    fun `isTokenValid returns true for matching email`() {
        val email = "user@example.com"
        val token = jwtService.generateToken(email, "SUBSCRIBER")
        assertTrue(jwtService.isTokenValid(token, email))
    }

    @Test
    fun `isTokenValid returns false for wrong email`() {
        val token = jwtService.generateToken("user@example.com", "SUBSCRIBER")
        assertFalse(jwtService.isTokenValid(token, "other@example.com"))
    }

    @Test
    fun `expired token throws exception`() {
        val expiredJwt = JwtService(secret, -1000L)
        val token = expiredJwt.generateToken("user@example.com", "SUBSCRIBER")

        assertThrows<Exception> {
            jwtService.isTokenValid(token, "user@example.com")
        }
    }

    @Test
    fun `tampered token throws exception`() {
        val token = jwtService.generateToken("user@example.com", "SUBSCRIBER")
        val tampered = token.dropLast(5) + "XXXXX"

        assertThrows<Exception> {
            jwtService.extractEmail(tampered)
        }
    }
}
