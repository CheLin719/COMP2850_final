package com.comp2850.goodfood.messages

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
class ApiMessagesControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    data class TestUser(
        val id: String,
        val email: String,
        val token: String
    )

    private fun registerAndLogin(
        role: String,
        firstName: String,
        proId: String? = null,
        licence: String? = null
    ): TestUser {
        val email = "${firstName.lowercase()}-${UUID.randomUUID()}@test.com"

        val request = mutableMapOf<String, Any?>(
            "firstName" to firstName,
            "lastName" to "User",
            "email" to email,
            "password" to "Password1!",
            "role" to role
        )

        if (proId != null) request["proId"] = proId
        if (licence != null) {
            request["licenceNo"] = licence
        }

        val registerResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk).andReturn()

        val registerBody = objectMapper.readValue(registerResult.response.contentAsString, Map::class.java)
        val id = registerBody["userId"].toString()
        val token = registerBody["token"] as String

        return TestUser(id = id, email = email, token = token)
    }

    private fun createAssignedPair(): Pair<TestUser, TestUser> {
        val professional = registerAndLogin(
            role = "HEALTH_PROFESSIONAL",
            firstName = "MessagePro",
            licence = "LIC-${UUID.randomUUID()}"
        )

        val subscriber = registerAndLogin(
            role = "SUBSCRIBER",
            firstName = "MessageSub"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/clients/bind")
                .header("Authorization", "Bearer ${professional.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "userId" to subscriber.id
                )))
        ).andExpect(status().is2xxSuccessful)

        return professional to subscriber
    }

    @Test
    fun `professional can send message to assigned subscriber and subscriber can read it`() {
        val (professional, subscriber) = createAssignedPair()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/messages")
                .header("Authorization", "Bearer ${professional.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "receiverId" to subscriber.id,
                    "text" to "Please review your plan."
                )))
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/messages/${professional.id}")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].senderId").value(professional.id))
            .andExpect(jsonPath("$[0].receiverId").value(subscriber.id))
            .andExpect(jsonPath("$[0].text").value("Please review your plan."))
            .andExpect(jsonPath("$[0].read").value(true))
    }

    @Test
    fun `subscriber can send message to assigned professional`() {
        val (professional, subscriber) = createAssignedPair()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/messages")
                .header("Authorization", "Bearer ${subscriber.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "receiverId" to professional.id,
                    "text" to "Thanks for the update."
                )))
        ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
    }

    @Test
    fun `message with html is rejected`() {
        val (professional, subscriber) = createAssignedPair()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/messages")
                .header("Authorization", "Bearer ${professional.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "receiverId" to subscriber.id,
                    "text" to "<script>alert('x')</script>"
                )))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `unassigned subscriber cannot message professional`() {
        val professional = registerAndLogin(
            role = "HEALTH_PROFESSIONAL",
            firstName = "OtherPro",
            licence = "LIC-${UUID.randomUUID()}"
        )

        val unrelatedSubscriber = registerAndLogin(
            role = "SUBSCRIBER",
            firstName = "OtherSub"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/messages")
                .header("Authorization", "Bearer ${unrelatedSubscriber.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "receiverId" to professional.id,
                    "text" to "Can I message you?"
                )))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `unauthenticated message request is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "receiverId" to "missing",
                    "text" to "No token"
                )))
        ).andExpect(status().is4xxClientError)
    }
}
