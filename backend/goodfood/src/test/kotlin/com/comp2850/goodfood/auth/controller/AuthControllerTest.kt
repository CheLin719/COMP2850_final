package com.comp2850.goodfood.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    @Test
    fun `register with valid subscriber data returns 200 and token`() {
        val request = mapOf(
            "firstName" to "Test",
            "lastName" to "User",
            "email" to "register@test.com",
            "password" to "Password1!",
            "role" to "SUBSCRIBER"
        )

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.token") { exists() }
        }
    }

    @Test
    fun `login with correct credentials returns 200 and token`() {
        val registerRequest = mapOf(
            "firstName" to "Login",
            "lastName" to "User",
            "email" to "login@test.com",
            "password" to "Password1!",
            "role" to "SUBSCRIBER"
        )

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isOk() }
        }

        val loginRequest = mapOf(
            "email" to "login@test.com",
            "password" to "Password1!"
        )

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.token") { exists() }
        }
    }

    @Test
    fun `register with weak password returns 400`() {
        val request = mapOf(
            "firstName" to "Weak",
            "lastName" to "User",
            "email" to "weak@test.com",
            "password" to "weak",
            "role" to "SUBSCRIBER"
        )

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `login with wrong password returns 401`() {
        val registerRequest = mapOf(
            "firstName" to "Wrong",
            "lastName" to "Password",
            "email" to "wrongpass@test.com",
            "password" to "Password1!",
            "role" to "SUBSCRIBER"
        )

        mockMvc.post("/api/auth/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isOk() }
        }

        val loginRequest = mapOf(
            "email" to "wrongpass@test.com",
            "password" to "WrongPass1!"
        )

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isUnauthorized() }
        }
    }

    @Test
    fun `login with non existent email returns 401`() {
        val loginRequest = mapOf(
            "email" to "nobody@test.com",
            "password" to "Password1!"
        )

        mockMvc.post("/api/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isUnauthorized() }
        }
    }
}
