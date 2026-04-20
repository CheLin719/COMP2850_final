package com.comp2850.goodfood.comments

import com.comp2850.goodfood.recipes.RecipeJpaRepository
import com.comp2850.goodfood.user.repository.UserStore
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/comments")
class ApiCommentsController(
    private val commentJpaRepository: CommentJpaRepository,
    private val recipeJpaRepository: RecipeJpaRepository,
    private val userStore: UserStore
) {

    @GetMapping
    fun getComments(
        @RequestParam recipeId: Long?
    ): List<ApiCommentResponse> {
        val actualRecipeId = recipeId
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "recipeId is required")

        recipeJpaRepository.findById(actualRecipeId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "recipe not found")

        return commentJpaRepository.findAllByRecipeIdOrderByCreatedAtDesc(actualRecipeId)
            .map { comment ->
                val userName = userStore.findById(comment.userId)?.name ?: "Unknown user"

                ApiCommentResponse(
                    id = comment.id ?: 0L,
                    user = userName,
                    text = comment.text,
                    createdAt = comment.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                )
            }
    }

    @PostMapping
    fun addComment(
        authentication: Authentication,
        @Valid @RequestBody request: ApiCommentCreateRequest
    ): ResponseEntity<Map<String, Any?>> {
        val user = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        recipeJpaRepository.findById(request.recipeId).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "recipe not found")

        val cleanedText = request.text.trim()

        if (containsHtml(cleanedText)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "HTML tags are not allowed in comments")
        }

        val saved = commentJpaRepository.save(
            CommentEntity(
                userId = user.id,
                recipeId = request.recipeId,
                text = cleanedText
            )
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(
            mapOf("id" to (saved.id ?: 0L))
        )
    }

    private fun containsHtml(text: String): Boolean {
        return Regex("<[^>]+>").containsMatchIn(text)
    }
}

data class ApiCommentCreateRequest(
    @field:NotNull(message = "recipeId is required")
    val recipeId: Long,

    @field:NotBlank(message = "text cannot be blank")
    @field:Size(max = 500, message = "text must be at most 500 characters")
    val text: String
)

data class ApiCommentResponse(
    val id: Long,
    val user: String,
    val text: String,
    val createdAt: String
)