package com.comp2850.goodfood.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.MalformedJwtException
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class SecurityTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    @Test
    fun `protected endpoints reject requests without token`() {
        val protectedEndpoints = listOf(
            "/api/clients",
            "/api/diary",
            "/api/appointments",
            "/api/favourites"
        )

        for (endpoint in protectedEndpoints) {
            mockMvc.perform(
                MockMvcRequestBuilders.get(endpoint)
            ).andExpect(
                status().is4xxClientError
            )
        }
    }

    @Test
    fun `malformed bearer token throws security exception`() {
        assertThrows(MalformedJwtException::class.java) {
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/diary")
                    .header("Authorization", "Bearer this.is.not.a.valid.jwt")
            )
        }
    }

    @Test
    fun `invalid auth scheme is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/diary")
                .header("Authorization", "InvalidScheme sometoken")
        ).andExpect(
            status().is4xxClientError
        )
    }

    @Test
    fun `register endpoint is publicly accessible`() {
        val request = mapOf(
            "firstName" to "Public",
            "lastName" to "User",
            "email" to "public-security@test.com",
            "password" to "Password1!",
            "role" to "SUBSCRIBER"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(
            status().isOk
        )
    }
}
