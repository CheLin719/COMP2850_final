package com.comp2850.goodfood.nutrition

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NutritionControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    private fun registerSubscriberAndGetToken(): String {
        val email = "nutrition-${UUID.randomUUID()}@test.com"

        val registerResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "firstName" to "Nutrition",
                    "lastName" to "Tester",
                    "email" to email,
                    "password" to "Password1!",
                    "role" to "SUBSCRIBER"
                )))
        ).andExpect(status().isOk).andReturn()

        val body = objectMapper.readValue(registerResult.response.contentAsString, Map::class.java)
        return body["token"] as String
    }

    @Test
    fun `get nutrition guides returns 200`() {
        val token = registerSubscriberAndGetToken()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/nutrition/guides")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)
    }

    @Test
    fun `get all food nutrition returns 200`() {
        val token = registerSubscriberAndGetToken()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/nutrition/foods")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)
    }

    @Test
    fun `search food nutrition by name returns 200`() {
        val token = registerSubscriberAndGetToken()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/nutrition/foods")
                .header("Authorization", "Bearer $token")
                .param("name", "chicken")
        ).andExpect(status().isOk)
    }

    @Test
    fun `get food suggestions returns 200`() {
        val token = registerSubscriberAndGetToken()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/nutrition/foods/suggestions")
                .header("Authorization", "Bearer $token")
                .param("name", "ch")
        ).andExpect(status().isOk)
    }

    @Test
    fun `get nutrition summary returns 200`() {
        val token = registerSubscriberAndGetToken()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/nutrition/summary")
                .header("Authorization", "Bearer $token")
                .param("date", "2026-05-02")
        ).andExpect(status().isOk)
    }

    @Test
    fun `get nutrition feedback returns 200`() {
        val token = registerSubscriberAndGetToken()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/nutrition/feedback")
                .header("Authorization", "Bearer $token")
                .param("days", "5")
        ).andExpect(status().isOk)
    }

    @Test
    fun `get nutrition trends returns 200`() {
        val token = registerSubscriberAndGetToken()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/nutrition/trends")
                .header("Authorization", "Bearer $token")
                .param("days", "7")
        ).andExpect(status().isOk)
    }

    @Test
    fun `get daily nutrition status returns 200`() {
        val token = registerSubscriberAndGetToken()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/nutrition/status")
                .header("Authorization", "Bearer $token")
                .param("date", "2026-05-02")
        ).andExpect(status().isOk)
    }

    @Test
    fun `get today nutrition status returns 200`() {
        val token = registerSubscriberAndGetToken()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/nutrition/status/today")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)
    }

    @Test
    fun `unauthenticated nutrition summary is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/nutrition/summary")
        ).andExpect(status().is4xxClientError)
    }
}
