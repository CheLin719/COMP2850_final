package com.comp2850.goodfood.recipes

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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApiRecipeControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    private fun registerAndLogin(email: String = "recipes@test.com"): String {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "firstName" to "Recipe",
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
    fun `authenticated user can get all recipes`() {
        val token = registerAndLogin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/recipes")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)
    }

    @Test
    fun `authenticated user can create recipe with valid data`() {
        val token = registerAndLogin()

        val request = mapOf(
            "name" to "Test Oats",
            "emoji" to "🥣",
            "tag" to "Breakfast",
            "kcal" to 350,
            "cost" to "Low",
            "timeMin" to 10,
            "ingredients" to listOf("Oats", "Milk", "Banana"),
            "steps" to listOf("Mix ingredients", "Serve")
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/recipes")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
    }

    @Test
    fun `search recipes with blank query returns 400 when authenticated`() {
        val token = registerAndLogin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/recipes/search?q=")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `create recipe with negative kcal returns 400 when authenticated`() {
        val token = registerAndLogin()

        val request = mapOf(
            "name" to "Bad Recipe",
            "kcal" to -1,
            "ingredients" to listOf("Ingredient"),
            "steps" to listOf("Step")
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/recipes")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `unauthenticated recipes request is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/recipes")
        ).andExpect(status().is4xxClientError)
    }
}
