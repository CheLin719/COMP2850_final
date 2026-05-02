package com.comp2850.goodfood.clients

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
class ApiClientDiaryControllerBranchTest {

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

    private fun registerProfessional(firstName: String): TestUser {
        return registerUser(
            role = "HEALTH_PROFESSIONAL",
            firstName = firstName,
            licenceNo = "LIC-${UUID.randomUUID()}"
        )
    }

    private fun registerSubscriber(firstName: String): TestUser {
        return registerUser(
            role = "SUBSCRIBER",
            firstName = firstName
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
    fun `subscriber cannot view client diary`() {
        val subscriber = registerSubscriber("DiaryForbiddenSub")
        val otherSubscriber = registerSubscriber("DiaryForbiddenOtherSub")

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/clients/${otherSubscriber.id}/diary")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `professional viewing missing client diary returns 404`() {
        val pro = registerProfessional("MissingDiaryPro")

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/clients/missing-client-id/diary")
                .header("Authorization", "Bearer ${pro.token}")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `professional cannot view diary for unassigned subscriber`() {
        val pro = registerProfessional("UnassignedDiaryPro")
        val subscriber = registerSubscriber("UnassignedDiarySub")

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/clients/${subscriber.id}/diary")
                .header("Authorization", "Bearer ${pro.token}")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `professional cannot view diary for another professionals client`() {
        val ownerPro = registerProfessional("DiaryOwnerPro")
        val otherPro = registerProfessional("DiaryOtherPro")
        val subscriber = registerSubscriber("DiaryOwnedSub")

        bindClient(ownerPro, subscriber)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/clients/${subscriber.id}/diary")
                .header("Authorization", "Bearer ${otherPro.token}")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `client diary returns 400 when from date is after to date`() {
        val pro = registerProfessional("DiaryBadDatePro")
        val subscriber = registerSubscriber("DiaryBadDateSub")

        bindClient(pro, subscriber)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/clients/${subscriber.id}/diary")
                .header("Authorization", "Bearer ${pro.token}")
                .param("from", "2030-02-01")
                .param("to", "2030-01-01")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `professional can view assigned client diary with default date range`() {
        val pro = registerProfessional("DiaryDefaultRangePro")
        val subscriber = registerSubscriber("DiaryDefaultRangeSub")

        bindClient(pro, subscriber)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/clients/${subscriber.id}/diary")
                .header("Authorization", "Bearer ${pro.token}")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.summary.totalEntries").value(0))
            .andExpect(jsonPath("$.summary.totalExerciseEntries").value(0))
            .andExpect(jsonPath("$.summary.status").value("inactive"))
    }

    @Test
    fun `professional can view assigned client diary with explicit date range`() {
        val pro = registerProfessional("DiaryExplicitRangePro")
        val subscriber = registerSubscriber("DiaryExplicitRangeSub")

        bindClient(pro, subscriber)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/clients/${subscriber.id}/diary")
                .header("Authorization", "Bearer ${pro.token}")
                .param("from", "2030-01-01")
                .param("to", "2030-01-31")
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.meals").isArray)
            .andExpect(jsonPath("$.exercise").isArray)
            .andExpect(jsonPath("$.summary.totalCalories").value(0))
            .andExpect(jsonPath("$.summary.totalProtein").value(0.0))
            .andExpect(jsonPath("$.summary.totalSugar").value(0.0))
            .andExpect(jsonPath("$.summary.totalExerciseKcal").value(0))
    }
}
