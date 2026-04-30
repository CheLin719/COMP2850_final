package com.comp2850.goodfood.exercise

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApiExerciseControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    private fun registerAndLogin(email: String = "exercise@test.com"): String {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "firstName" to "Exercise",
                    "lastName" to "User",
                    "email" to email,
                    "password" to "Password1!",
                    "role" to "SUBSCRIBER"
                )))
        ).andExpect(status().isOk)

        val login = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "email" to email,
                    "password" to "Password1!"
                )))
        ).andExpect(status().isOk).andReturn()

        val body = objectMapper.readValue(login.response.contentAsString, Map::class.java)
        return body["token"] as String
    }

    @Test
    fun `authenticated user can get exercise list`() {
        val token = registerAndLogin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/exercise")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)
    }

    @Test
    fun `authenticated user can create exercise entry`() {
        val token = registerAndLogin()

        val request = mapOf(
            "date" to LocalDate.now().toString(),
            "activity" to "Running",
            "duration" to 30,
            "kcal" to 250
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/exercise")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
    }

    @Test
    fun `create exercise with negative kcal returns 400`() {
        val token = registerAndLogin()

        val request = mapOf(
            "date" to LocalDate.now().toString(),
            "activity" to "Running",
            "duration" to 30,
            "kcal" to -10
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/exercise")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `unauthenticated exercise request is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/exercise")
        ).andExpect(status().is4xxClientError)
    }
}
