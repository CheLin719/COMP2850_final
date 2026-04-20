package com.comp2850.goodfood.recipes

import com.comp2850.goodfood.comments.CommentJpaRepository
import com.comp2850.goodfood.ratings.RatingJpaRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import kotlin.math.round

@RestController
@RequestMapping("/api/recipes")
class ApiRecipeController(
    private val recipeJpaRepository: RecipeJpaRepository,
    private val ratingJpaRepository: RatingJpaRepository,
    private val commentJpaRepository: CommentJpaRepository
) {

    @GetMapping
    fun getAllRecipes(): List<ApiRecipeResponse> {
        return recipeJpaRepository.findAll()
            .sortedBy { it.id }
            .map { it.toApiResponse() }
    }

    @GetMapping("/search")
    fun searchRecipes(
        @RequestParam q: String?
    ): List<ApiRecipeResponse> {
        if (q.isNullOrBlank()) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "q must not be blank"
            )
        }

        val terms = q.split(",", " ")
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
            .distinct()

        if (terms.isEmpty()) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "q must not be blank"
            )
        }

        return recipeJpaRepository.findAll()
            .map { entity ->
                val ingredients = parseJsonLikeArray(entity.ingredients)
                val searchable = buildList {
                    add(entity.name)
                    entity.tag?.let { add(it) }
                    addAll(ingredients)
                }.joinToString(" ").lowercase()

                val matchCount = terms.count { term -> searchable.contains(term) }

                entity to matchCount
            }
            .filter { (_, matchCount) -> matchCount > 0 }
            .sortedWith(
                compareByDescending<Pair<RecipeEntity, Int>> { it.second }
                    .thenBy { it.first.id ?: 0L }
            )
            .map { (entity, _) -> entity.toApiResponse() }
    }

    @PostMapping
    fun createRecipe(
        @Valid @RequestBody request: ApiRecipeCreateRequest
    ): Map<String, Any?> {
        if (request.kcal != null && request.kcal < 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "kcal must be 0 or greater")
        }

        if (request.timeMin != null && request.timeMin <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "timeMin must be greater than 0")
        }

        val entity = RecipeEntity(
            name = request.name.trim(),
            emoji = request.emoji?.trim()?.ifBlank { null },
            tag = request.tag?.trim()?.ifBlank { null },
            kcal = request.kcal,
            cost = request.cost?.trim()?.ifBlank { null },
            timeMin = request.timeMin,
            ingredients = toJsonLikeArray(request.ingredients),
            steps = toJsonLikeArray(request.steps)
        )

        val saved = recipeJpaRepository.save(entity)

        return mapOf("id" to (saved.id ?: 0L))
    }

    private fun RecipeEntity.toApiResponse(): ApiRecipeResponse {
        val recipeId = this.id ?: 0L
        val ratings = ratingJpaRepository.findAllByIdRecipeId(recipeId)
        val ratingCount = ratings.size
        val averageRating = if (ratingCount == 0) {
            0.0
        } else {
            round(ratings.map { it.score }.average() * 10) / 10
        }

        val commentCount = commentJpaRepository.countByRecipeId(recipeId).toInt()

        return ApiRecipeResponse(
            id = recipeId,
            name = this.name,
            emoji = this.emoji,
            tag = this.tag,
            kcal = this.kcal,
            cost = this.cost,
            timeMin = this.timeMin,
            ingredients = parseJsonLikeArray(this.ingredients),
            steps = parseJsonLikeArray(this.steps),
            averageRating = averageRating,
            ratingCount = ratingCount,
            commentCount = commentCount
        )
    }

    private fun parseJsonLikeArray(value: String): List<String> {
        val trimmed = value.trim()

        if (trimmed.isBlank() || trimmed == "[]") {
            return emptyList()
        }

        return trimmed
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .map { it.trim().removePrefix("\"").removeSuffix("\"") }
            .filter { it.isNotBlank() }
    }

    private fun toJsonLikeArray(values: List<String>): String {
        return values
            .map { "\"${it.trim().replace("\"", "\\\"")}\"" }
            .joinToString(prefix = "[", postfix = "]", separator = ",")
    }
}

data class ApiRecipeResponse(
    val id: Long,
    val name: String,
    val emoji: String?,
    val tag: String?,
    val kcal: Int?,
    val cost: String?,
    val timeMin: Int?,
    val ingredients: List<String>,
    val steps: List<String>,
    val averageRating: Double,
    val ratingCount: Int,
    val commentCount: Int
)

data class ApiRecipeCreateRequest(
    @field:NotBlank(message = "name cannot be blank")
    val name: String,

    val emoji: String? = null,
    val tag: String? = null,
    val kcal: Int? = null,
    val cost: String? = null,
    val timeMin: Int? = null,

    @field:NotEmpty(message = "ingredients cannot be empty")
    val ingredients: List<String>,

    @field:NotEmpty(message = "steps cannot be empty")
    val steps: List<String>
)