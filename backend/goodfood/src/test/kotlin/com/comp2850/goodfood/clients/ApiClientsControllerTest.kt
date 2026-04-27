package com.comp2850.goodfood.clients

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
class ApiClientsControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `unauthenticated request to get clients is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/clients")
        ).andExpect(
            status().is4xxClientError
        )
    }

    @Test
    fun `unauthenticated request to bind client is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/clients/bind")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"userId":"1"}""")
        ).andExpect(
            status().is4xxClientError
        )
    }

    @Test
    fun `unauthenticated request to unbind client is rejected`() {
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/clients/1")
        ).andExpect(
            status().is4xxClientError
        )
    }
}
