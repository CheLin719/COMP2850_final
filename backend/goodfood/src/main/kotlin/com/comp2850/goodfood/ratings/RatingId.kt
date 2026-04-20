package com.comp2850.goodfood.ratings

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable

@Embeddable
data class RatingId(
    @Column(name = "user_id")
    var userId: String = "",

    @Column(name = "recipe_id")
    var recipeId: Long = 0L
) : Serializable