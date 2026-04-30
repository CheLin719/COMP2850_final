package com.comp2850.goodfood.appointments

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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApiAppointmentsControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()

    private fun registerAndLogin(email: String = "appointments@test.com"): String {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "firstName" to "Appointment",
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
    fun `authenticated user can get appointments`() {
        val token = registerAndLogin()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/appointments")
                .header("Authorization", "Bearer $token")
        ).andExpect(status().isOk)
    }

    @Test
    fun `subscriber cannot create appointment`() {
        val token = registerAndLogin()

        val request = mapOf(
            "clientId" to "some-client-id",
            "date" to "2026-05-01",
            "time" to "10:00",
            "type" to "Consultation"
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/appointments")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `unauthenticated appointments request is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/appointments")
        ).andExpect(status().is4xxClientError)
    }
}
