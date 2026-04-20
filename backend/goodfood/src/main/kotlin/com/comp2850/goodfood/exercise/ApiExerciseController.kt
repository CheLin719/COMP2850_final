package com.comp2850.goodfood.exercise

import com.fasterxml.jackson.annotation.JsonFormat
import com.comp2850.goodfood.user.repository.UserStore
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@RestController
@RequestMapping("/api/exercise")
class ApiExerciseController(
    private val exerciseJpaRepository: ExerciseJpaRepository,
    private val userStore: UserStore
) {

    @GetMapping
    fun getMyExercise(
        authentication: Authentication,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        date: LocalDate?
    ): List<ApiExerciseResponse> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val items = if (date == null) {
            exerciseJpaRepository.findAllByUserIdOrderByDateDescCreatedAtDesc(currentUser.id)
        } else {
            exerciseJpaRepository.findAllByUserIdAndDateOrderByCreatedAtDesc(currentUser.id, date)
        }

        return items.map { it.toResponse() }
    }

    @PostMapping
    fun createExercise(
        authentication: Authentication,
        @Valid @RequestBody request: ApiExerciseCreateRequest
    ): ResponseEntity<Map<String, Any?>> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val saved = exerciseJpaRepository.save(
            ExerciseEntity(
                userId = currentUser.id,
                date = request.date,
                activity = request.activity.trim(),
                duration = request.duration,
                kcal = request.kcal
            )
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(
            mapOf("id" to (saved.id ?: 0L))
        )
    }

    @DeleteMapping("/{id}")
    fun deleteExercise(
        authentication: Authentication,
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val exercise = exerciseJpaRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "exercise not found")

        if (exercise.userId != currentUser.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "you cannot delete this exercise entry")
        }

        exerciseJpaRepository.delete(exercise)
        return ResponseEntity.noContent().build()
    }

    private fun ExerciseEntity.toResponse(): ApiExerciseResponse {
        return ApiExerciseResponse(
            id = this.id ?: 0L,
            date = this.date.toString(),
            activity = this.activity,
            duration = this.duration,
            kcal = this.kcal
        )
    }
}

data class ApiExerciseCreateRequest(
    @field:NotNull(message = "date is required")
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate,

    @field:NotBlank(message = "activity cannot be blank")
    val activity: String,

    @field:Min(value = 1, message = "duration must be greater than 0")
    val duration: Int,

    @field:Min(value = 1, message = "kcal must be greater than 0")
    val kcal: Int
)

data class ApiExerciseResponse(
    val id: Long,
    val date: String,
    val activity: String,
    val duration: Int,
    val kcal: Int
)