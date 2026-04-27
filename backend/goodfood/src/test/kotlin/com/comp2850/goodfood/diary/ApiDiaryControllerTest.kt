package com.comp2850.goodfood.diary

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
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
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ApiDiaryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private val objectMapper = ObjectMapper()
    private lateinit var token: String

    @BeforeEach
    fun setUp() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "firstName" to "Diary",
                    "lastName" to "User",
                    "email" to "diary@test.com",
                    "password" to "Password1!",
                    "role" to "SUBSCRIBER"
                )))
        ).andExpect(
            status().isOk
        )

        val login = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mapOf(
                    "email" to "diary@test.com",
                    "password" to "Password1!"
                )))
        ).andExpect(
            status().isOk
        ).andReturn()

        val body = objectMapper.readValue(login.response.contentAsString, Map::class.java)
        token = body["token"] as String
    }

    @Test
    fun `authenticated user can get diary for today`() {
        val today = LocalDate.now().toString()

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/diary?date=$today")
                .header("Authorization", "Bearer $token")
        ).andExpect(
            status().is2xxSuccessful
        )
    }

    @Test
    fun `authenticated user can add diary entry`() {
        val entry = mapOf(
            "date" to LocalDate.now().toString(),
            "mealType" to "BREAKFAST",
            "foodName" to "Oats",
            "kcal" to 350,
            "protein" to 10,
            "carbs" to 60,
            "fat" to 5,
            "sugar" to 8
        )

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/diary")
                .header("Authorization", "Bearer $token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(entry))
        ).andExpect(
            status().is2xxSuccessful
        )
    }

    @Test
    fun `unauthenticated diary request is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/diary")
        ).andExpect(
            status().is4xxClientError
        )
    }
}
