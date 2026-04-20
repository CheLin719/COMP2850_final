package com.comp2850.goodfood.favourites

import org.springframework.data.jpa.repository.JpaRepository

interface FavouriteJpaRepository : JpaRepository<FavouriteEntity, FavouriteId> {
    fun findAllByIdUserId(userId: String): List<FavouriteEntity>
    fun existsByIdUserIdAndIdRecipeId(userId: String, recipeId: Long): Boolean
    fun deleteByIdUserIdAndIdRecipeId(userId: String, recipeId: Long)
}