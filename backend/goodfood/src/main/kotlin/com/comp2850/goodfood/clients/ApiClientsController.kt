package com.comp2850.goodfood.clients

import com.comp2850.goodfood.diary.DiaryStore
import com.comp2850.goodfood.user.Role
import com.comp2850.goodfood.user.repository.UserStore
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/clients")
class ApiClientsController(
    private val userStore: UserStore,
    private val diaryStore: DiaryStore
) {

    @GetMapping
    fun getClients(authentication: Authentication): List<ApiClientResponse> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        if (currentUser.role != Role.HEALTH_PROFESSIONAL) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "only professionals can access clients")
        }

        return userStore.findAll()
            .filter { it.role == Role.SUBSCRIBER && it.proId == currentUser.id }
            .sortedBy { it.name.lowercase() }
            .map { client ->
                val diaries = diaryStore.findByUserEmail(client.email)
                val lastDiaryDate = diaries.maxByOrNull { it.diaryDate }?.diaryDate?.toString()
                val diaryCount = diaries.size

                ApiClientResponse(
                    userId = client.id,
                    name = client.name,
                    email = client.email,
                    stats = ApiClientStats(
                        lastDiaryDate = lastDiaryDate,
                        diaryCount = diaryCount,
                        status = toStatus(lastDiaryDate, diaryCount)
                    )
                )
            }
    }

    private fun toStatus(lastDiaryDate: String?, diaryCount: Int): String {
        return when {
            diaryCount == 0 -> "inactive"
            lastDiaryDate == null -> "inactive"
            else -> "active"
        }
    }
}

data class ApiClientResponse(
    val userId: String,
    val name: String,
    val email: String,
    val stats: ApiClientStats
)

data class ApiClientStats(
    val lastDiaryDate: String?,
    val diaryCount: Int,
    val status: String
)