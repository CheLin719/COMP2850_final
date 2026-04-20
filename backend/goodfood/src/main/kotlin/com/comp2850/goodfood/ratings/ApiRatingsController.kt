package com.comp2850.goodfood.ratings

import com.comp2850.goodfood.recipes.RecipeJpaRepository
import com.comp2850.goodfood.user.repository.UserStore
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/ratings")
class ApiRatingsController(
    private val ratingJpaRepository: RatingJpaRepository,
    private val recipeJpaRepository: RecipeJpaRepository,
    private val userStore: UserStore
) {

    @PostMapping
    fun upsertRating(
        authentication: Authentication,
        @Valid @RequestBody request: ApiRatingUpsertRequest
    ): ResponseEntity<Map<String, Any?>> {
        val user = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val recipe = recipeJpaRepository.findById(request.recipeId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "recipe not found")

        val recipeId = recipe.id ?: 0L

        val existing = ratingJpaRepository.findByIdUserIdAndIdRecipeId(user.id, recipeId)

        val saved = if (existing != null) {
            existing.score = request.score
            ratingJpaRepository.save(existing)
        } else {
            ratingJpaRepository.save(
                RatingEntity(
                    id = RatingId(
                        userId = user.id,
                        recipeId = recipeId
                    ),
                    score = request.score
                )
            )
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            mapOf(
                "recipeId" to saved.id.recipeId,
                "score" to saved.score
            )
        )
    }
}

data class ApiRatingUpsertRequest(
    @field:NotNull(message = "recipeId is required")
    val recipeId: Long,

    @field:NotNull(message = "score is required")
    @field:Min(value = 1, message = "score must be between 1 and 5")
    @field:Max(value = 5, message = "score must be between 1 and 5")
    val score: Int
)