package com.comp2850.goodfood.comments

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
class ApiCommentsControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    private fun registerAndLogin(): String {
        val email = "comment-${UUID.randomUUID()}@test.com"

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "firstName" to "Comment",
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
            "name" to "Comment Test Recipe",
            "kcal" to 400,
            "ingredients" to listOf("Rice", "Chicken"),
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
    fun `authenticated user can add and get comments`() {
        val token = registerAndLogin()
        val recipeId = createRecipe(token)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/comments")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "recipeId" to recipeId,
                    "text" to "Nice recipe"
                )))
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/comments")
                .header("Authorization", "Bearer $token")
                .param("recipeId", recipeId.toString())
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].text").value("Nice recipe"))
    }

    @Test
    fun `blank comment text returns 400`() {
        val token = registerAndLogin()
        val recipeId = createRecipe(token)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/comments")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "recipeId" to recipeId,
                    "text" to ""
                )))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `html comment text returns 400`() {
        val token = registerAndLogin()
        val recipeId = createRecipe(token)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/comments")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "recipeId" to recipeId,
                    "text" to "<script>alert('x')</script>"
                )))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `getting comments without recipe id returns 400`() {
        val token = registerAndLogin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/comments")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `unauthenticated comment creation is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "recipeId" to 1,
                    "text" to "No token"
                )))
        ).andExpect(status().is4xxClientError)
    }
}
