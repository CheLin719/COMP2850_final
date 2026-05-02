package com.comp2850.goodfood.ratings

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiRatingsControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    private fun registerAndLogin(): String {
        val email = "rating-${UUID.randomUUID()}@test.com"

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "firstName" to "Rating",
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

    private fun createRecipe(token: String): Long {
        val request = mapOf(
            "name" to "Rating Test Recipe",
            "kcal" to 500,
            "ingredients" to listOf("Pasta", "Tomato"),
            "steps" to listOf("Cook", "Serve")
        )

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/recipes")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk).andReturn()

        val body = objectMapper.readValue(result.response.contentAsString, Map::class.java)
        return (body["id"] as Number).toLong()
    }

    @Test
    fun `authenticated user can create and update rating`() {
        val token = registerAndLogin()
        val recipeId = createRecipe(token)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/ratings")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "recipeId" to recipeId,
                    "score" to 4
                )))
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.recipeId").value(recipeId.toInt()))
            .andExpect(jsonPath("$.score").value(4))

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/ratings")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "recipeId" to recipeId,
                    "score" to 5
                )))
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.recipeId").value(recipeId.toInt()))
            .andExpect(jsonPath("$.score").value(5))
    }

    @Test
    fun `rating score below range returns 400`() {
        val token = registerAndLogin()
        val recipeId = createRecipe(token)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/ratings")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "recipeId" to recipeId,
                    "score" to 0
                )))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `rating missing recipe returns 404`() {
        val token = registerAndLogin()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/ratings")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "recipeId" to 999999,
                    "score" to 4
                )))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `unauthenticated rating request is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "recipeId" to 1,
                    "score" to 5
                )))
        ).andExpect(status().is4xxClientError)
    }
}
