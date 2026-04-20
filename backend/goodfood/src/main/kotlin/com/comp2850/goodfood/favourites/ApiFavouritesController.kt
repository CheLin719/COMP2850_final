package com.comp2850.goodfood.favourites

import com.comp2850.goodfood.recipes.RecipeEntity
import com.comp2850.goodfood.recipes.RecipeJpaRepository
import com.comp2850.goodfood.user.repository.UserStore
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/favourites")
class ApiFavouritesController(
    private val favouriteJpaRepository: FavouriteJpaRepository,
    private val recipeJpaRepository: RecipeJpaRepository,
    private val userStore: UserStore
) {

    @GetMapping
    fun getFavourites(authentication: Authentication): List<ApiFavouriteResponse> {
        val user = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        return favouriteJpaRepository.findAllByIdUserId(user.id)
            .sortedByDescending { it.id.recipeId }
            .mapNotNull { favourite ->
                val recipe = recipeJpaRepository.findById(favourite.id.recipeId).orElse(null)
                    ?: return@mapNotNull null

                ApiFavouriteResponse(
                    recipeId = favourite.id.recipeId,
                    recipe = recipe.toSummary()
                )
            }
    }

    @PostMapping
    fun addFavourite(
        authentication: Authentication,
        @Valid @RequestBody request: ApiFavouriteCreateRequest
    ): ResponseEntity<Map<String, Any?>> {
        val user = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val recipe = recipeJpaRepository.findById(request.recipeId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "recipe not found")

        val recipeId = recipe.id ?: 0L
        val exists = favouriteJpaRepository.existsByIdUserIdAndIdRecipeId(user.id, recipeId)

        if (!exists) {
            favouriteJpaRepository.save(
                FavouriteEntity(
                    id = FavouriteId(
                        userId = user.id,
                        recipeId = recipeId
                    )
                )
            )
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            mapOf("recipeId" to recipeId)
        )
    }

    @Transactional
    @DeleteMapping("/{recipeId}")
    fun removeFavourite(
        authentication: Authentication,
        @PathVariable recipeId: Long
    ): ResponseEntity<Void> {
        val user = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        favouriteJpaRepository.deleteByIdUserIdAndIdRecipeId(user.id, recipeId)
        return ResponseEntity.noContent().build()
    }

    private fun RecipeEntity.toSummary(): ApiFavouriteRecipeSummary {
        return ApiFavouriteRecipeSummary(
            id = this.id ?: 0L,
            name = this.name,
            emoji = this.emoji,
            tag = this.tag,
            kcal = this.kcal,
            cost = this.cost,
            timeMin = this.timeMin,
            ingredients = parseJsonLikeArray(this.ingredients),
            steps = parseJsonLikeArray(this.steps)
        )
    }

    private fun parseJsonLikeArray(value: String): List<String> {
        val trimmed = value.trim()
        if (trimmed.isBlank() || trimmed == "[]") return emptyList()

        return trimmed
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .map { it.trim().removePrefix("\"").removeSuffix("\"") }
            .filter { it.isNotBlank() }
    }
}

data class ApiFavouriteCreateRequest(
    @field:NotNull(message = "recipeId is required")
    val recipeId: Long
)

data class ApiFavouriteResponse(
    val recipeId: Long,
    val recipe: ApiFavouriteRecipeSummary
)

data class ApiFavouriteRecipeSummary(
    val id: Long,
    val name: String,
    val emoji: String?,
    val tag: String?,
    val kcal: Int?,
    val cost: String?,
    val timeMin: Int?,
    val ingredients: List<String>,
    val steps: List<String>
)