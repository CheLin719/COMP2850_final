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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiClientsControllerBranchTest {

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
    fun `subscriber cannot access client list`() {
        val subscriber = registerSubscriber("ClientListSub")

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/clients")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `subscriber cannot bind clients`() {
        val subscriber = registerSubscriber("BindSub")
        val otherSubscriber = registerSubscriber("BindTargetSub")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/clients/bind")
                .header("Authorization", "Bearer ${subscriber.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "userId" to otherSubscriber.id
                )))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `professional binding missing user returns 404`() {
        val pro = registerProfessional("MissingClientPro")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/clients/bind")
                .header("Authorization", "Bearer ${pro.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "userId" to "missing-client-id"
                )))
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `professional cannot bind another professional as client`() {
        val pro = registerProfessional("BindWrongRolePro")
        val otherPro = registerProfessional("BindTargetPro")

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/clients/bind")
                .header("Authorization", "Bearer ${pro.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "userId" to otherPro.id
                )))
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `professional cannot bind client already assigned to another professional`() {
        val ownerPro = registerProfessional("OwnerConflictPro")
        val otherPro = registerProfessional("OtherConflictPro")
        val subscriber = registerSubscriber("ConflictSub")

        bindClient(ownerPro, subscriber)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/clients/bind")
                .header("Authorization", "Bearer ${otherPro.token}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "userId" to subscriber.id
                )))
        ).andExpect(status().isConflict)
    }

    @Test
    fun `professional can list assigned clients`() {
        val pro = registerProfessional("ListAssignedPro")
        val subscriber = registerSubscriber("ListAssignedSub")

        bindClient(pro, subscriber)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/clients")
                .header("Authorization", "Bearer ${pro.token}")
        ).andExpect(status().isOk)
    }

    @Test
    fun `subscriber cannot unbind client from professional side`() {
        val subscriber = registerSubscriber("UnbindForbiddenSub")
        val otherSubscriber = registerSubscriber("UnbindForbiddenOtherSub")

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/clients/${otherSubscriber.id}/bind")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `professional cannot unbind client assigned to another professional`() {
        val ownerPro = registerProfessional("OwnerUnbindPro")
        val otherPro = registerProfessional("OtherUnbindPro")
        val subscriber = registerSubscriber("UnbindOtherSub")

        bindClient(ownerPro, subscriber)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/clients/${subscriber.id}/bind")
                .header("Authorization", "Bearer ${otherPro.token}")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `professional unbinding missing user returns 404`() {
        val pro = registerProfessional("MissingUnbindPro")

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/clients/missing-user-id/bind")
                .header("Authorization", "Bearer ${pro.token}")
        ).andExpect(status().isNotFound)
    }

    @Test
    fun `professional can unbind own client`() {
        val pro = registerProfessional("OwnUnbindPro")
        val subscriber = registerSubscriber("OwnUnbindSub")

        bindClient(pro, subscriber)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/clients/${subscriber.id}/bind")
                .header("Authorization", "Bearer ${pro.token}")
        ).andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `professional cannot use subscriber self unbind endpoint`() {
        val pro = registerProfessional("SelfUnbindWrongRolePro")

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/clients/self-unbind")
                .header("Authorization", "Bearer ${pro.token}")
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `subscriber self unbind without professional returns 400`() {
        val subscriber = registerSubscriber("SelfUnbindNoProSub")

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/clients/self-unbind")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().isBadRequest)
    }

    @Test
    fun `subscriber can self unbind from professional`() {
        val pro = registerProfessional("SelfUnbindPro")
        val subscriber = registerSubscriber("SelfUnbindSub")

        bindClient(pro, subscriber)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/clients/self-unbind")
                .header("Authorization", "Bearer ${subscriber.token}")
        ).andExpect(status().is2xxSuccessful)
    }
}
