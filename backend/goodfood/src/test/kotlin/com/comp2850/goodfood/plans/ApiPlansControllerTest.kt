package com.comp2850.goodfood.plans

import com.fasterxml.jackson.databind.ObjectMapper
import com.comp2850.goodfood.user.repository.UserStore
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
class ApiPlansControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userStore: UserStore

    private val objectMapper = ObjectMapper()

    private fun registerAndLogin(email: String = "plans@test.com"): Pair<String, String> {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "firstName" to "Plans",
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
        val token = body["token"] as String
        val userId = userStore.findByEmail(email)?.id
            ?: throw IllegalStateException("Could not find registered user")

        return token to userId
    }

    @Test
    fun `subscriber can view own plans`() {
        val (token, userId) = registerAndLogin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/plans/$userId")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)
    }

    @Test
    fun `subscriber cannot view another users plans`() {
        val (token, _) = registerAndLogin("plans-a@test.com")
        val (_, otherUserId) = registerAndLogin("plans-b@test.com")

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/plans/$otherUserId")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `unauthenticated plans request is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/plans/some-user-id")
        ).andExpect(status().is4xxClientError)
    }
}
