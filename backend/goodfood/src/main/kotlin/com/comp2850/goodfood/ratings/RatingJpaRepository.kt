package com.comp2850.goodfood.ratings

import org.springframework.data.jpa.repository.JpaRepository

interface RatingJpaRepository : JpaRepository<RatingEntity, RatingId> {
    fun findByIdUserIdAndIdRecipeId(userId: String, recipeId: Long): RatingEntity?
    fun findAllByIdRecipeId(recipeId: Long): List<RatingEntity>
    fun countByIdRecipeId(recipeId: Long): Long
}