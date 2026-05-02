package com.comp2850.goodfood.notifications

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
class ApiNotificationsControllerTest {

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

    private fun createNotificationForSubscriber(): TestUser {
        val professional = registerAndLogin(
            role = "HEALTH_PROFESSIONAL",
            firstName = "NotifyPro",
            licence = "LIC-${UUID.randomUUID()}"
        )

        val subscriber = registerAndLogin(
            role = "SUBSCRIBER",
            firstName = "NotifySub"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/clients/bind")
                .header("Authorization", "Bearer ${professional.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "userId" to subscriber.id
                )))
        ).andExpect(status().is2xxSuccessful)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/messages")
                .header("Authorization", "Bearer ${professional.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "receiverId" to subscriber.id,
                    "text" to "Notification trigger message"
                )))
        ).andExpect(status().isCreated)

        return subscriber
    }

    private fun firstNotificationId(token: String): Long {
        val result = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/notifications")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk).andReturn()

        val list = objectMapper.readValue(result.response.contentAsString, List::class.java)
        val first = list.first() as Map<*, *>
        return (first["id"] as Number).toLong()
    }

    @Test
    fun `user can list notifications and get unread count`() {
        val subscriber = createNotificationForSubscriber()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/notifications")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").exists())
            .andExpect(jsonPath("$[0].message").exists())
            .andExpect(jsonPath("$[0].read").value(false))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/notifications/unread-count")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.unreadCount").value(1))
    }

    @Test
    fun `user can filter unread notifications`() {
        val subscriber = createNotificationForSubscriber()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/notifications")
                .header("Authorization", "Bearer ${subscriber.token}")
                .param("unreadOnly", "true")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$[0].read").value(false))
    }

    @Test
    fun `user can mark notification as read`() {
        val subscriber = createNotificationForSubscriber()
        val notificationId = firstNotificationId(subscriber.token)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/notifications/$notificationId/read")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(notificationId.toInt()))
            .andExpect(jsonPath("$.read").value(true))
    }

    @Test
    fun `user can mark all notifications as read`() {
        val subscriber = createNotificationForSubscriber()

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/notifications/read-all")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("all notifications marked as read"))

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/notifications/unread-count")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.unreadCount").value(0))
    }

    @Test
    fun `user can delete own notification`() {
        val subscriber = createNotificationForSubscriber()
        val notificationId = firstNotificationId(subscriber.token)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/notifications/$notificationId")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.message").value("notification deleted"))
    }

    @Test
    fun `unauthenticated notifications request is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/notifications")
        ).andExpect(status().is4xxClientError)
    }
}
