package com.comp2850.goodfood.errorpaths

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
class ErrorPathControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    data class TestUser(
        val id: String,
        val token: String
    )

    private fun registerUser(
        role: String,
        firstName: String,
        licenceNo: String? = null
    ): TestUser {
        val email = "${firstName.lowercase()}-${UUID.randomUUID()}@test.com"

        val request = mutableMapOf<String, Any?>(
            "firstName" to firstName,
            "lastName" to "User",
            "email" to email,
            "password" to "Password1!",
            "role" to role
        )

        if (licenceNo != null) {
            request["licenceNo"] = licenceNo
        }

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk).andReturn()

        val body = objectMapper.readValue(result.response.contentAsString, Map::class.java)

        return TestUser(
            id = body["userId"].toString(),
            token = body["token"].toString()
        )
    }

    private fun bindClient(pro: TestUser, subscriber: TestUser) {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/clients/bind")
                .header("Authorization", "Bearer ${pro.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "userId" to subscriber.id
                )))
        ).andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `sending message to missing receiver returns 404`() {
        val subscriber = registerUser(
            role = "SUBSCRIBER",
            firstName = "MissingReceiverSub"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/messages")
                .header("Authorization", "Bearer ${subscriber.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "receiverId" to "missing-user-id",
                    "text" to "Hello"
                )))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `updating missing appointment returns 404`() {
        val subscriber = registerUser(
            role = "SUBSCRIBER",
            firstName = "MissingAppointmentSub"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/appointments/99999999")
                .header("Authorization", "Bearer ${subscriber.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "status" to "confirmed"
                )))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `updating appointment with invalid status returns 400`() {
        val pro = registerUser(
            role = "HEALTH_PROFESSIONAL",
            firstName = "InvalidStatusPro",
            licenceNo = "LIC-${UUID.randomUUID()}"
        )

        val subscriber = registerUser(
            role = "SUBSCRIBER",
            firstName = "InvalidStatusSub"
        )

        bindClient(pro, subscriber)

        val createResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/appointments")
                .header("Authorization", "Bearer ${pro.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "clientId" to subscriber.id,
                    "date" to "2030-01-10",
                    "time" to "09:30",
                    "type" to "Check-in"
                )))
        ).andExpect(status().isCreated).andReturn()

        val createBody = objectMapper.readValue(createResult.response.contentAsString, Map::class.java)
        val appointmentId = createBody["id"].toString()

        mockMvc.perform(
            MockMvcRequestBuilders.patch("/api/appointments/$appointmentId")
                .header("Authorization", "Bearer ${pro.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "status" to "invalid-status"
                )))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `professional cannot delete another professionals plan`() {
        val ownerPro = registerUser(
            role = "HEALTH_PROFESSIONAL",
            firstName = "PlanOwnerPro",
            licenceNo = "LIC-${UUID.randomUUID()}"
        )

        val otherPro = registerUser(
            role = "HEALTH_PROFESSIONAL",
            firstName = "OtherPlanPro",
            licenceNo = "LIC-${UUID.randomUUID()}"
        )

        val subscriber = registerUser(
            role = "SUBSCRIBER",
            firstName = "PlanClientSub"
        )

        bindClient(ownerPro, subscriber)

        val saveResult = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/plans/${subscriber.id}")
                .header("Authorization", "Bearer ${ownerPro.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "planType" to "meal",
                    "daysJson" to """{"monday":["oats","salad"]}""",
                    "targetKcal" to 2000,
                    "targetProtein" to 120,
                    "targetCarbsPct" to 50,
                    "targetFatPct" to 30,
                    "notes" to "Test meal plan"
                )))
        ).andExpect(status().is2xxSuccessful).andReturn()

        val saveBody = objectMapper.readValue(saveResult.response.contentAsString, Map::class.java)
        val planId = saveBody["id"].toString()

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/plans/$planId")
                .header("Authorization", "Bearer ${otherPro.token}")
        ).andExpect(status().isForbidden)
    }
}
