package com.comp2850.goodfood.favourites

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
class ApiFavouritesControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    private fun registerAndLogin(): String {
        val email = "fav-${UUID.randomUUID()}@test.com"

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "firstName" to "Favourite",
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
            "name" to "Favourite Test Recipe",
            "kcal" to 300,
            "ingredients" to listOf("Oats", "Milk"),
            "steps" to listOf("Mix", "Serve")
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
    fun `authenticated user can add and list favourite`() {
        val token = registerAndLogin()
        val recipeId = createRecipe(token)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/favourites")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf("recipeId" to recipeId)))
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.recipeId").value(recipeId.toInt()))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/favourites")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].recipeId").value(recipeId.toInt()))
            .andExpect(jsonPath("$[0].recipe.name").value("Favourite Test Recipe"))
    }

    @Test
    fun `adding missing recipe as favourite returns 404`() {
        val token = registerAndLogin()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/favourites")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf("recipeId" to 999999)))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `authenticated user can remove favourite`() {
        val token = registerAndLogin()
        val recipeId = createRecipe(token)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/favourites")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf("recipeId" to recipeId)))
        ).andExpect(status().isCreated)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/favourites/$recipeId")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isNoContent)
    }

    @Test
    fun `unauthenticated favourites request is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/favourites")
        ).andExpect(status().is4xxClientError)
    }
}
