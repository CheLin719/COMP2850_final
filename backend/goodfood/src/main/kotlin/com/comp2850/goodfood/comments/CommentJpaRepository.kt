package com.comp2850.goodfood.comments

import org.springframework.data.jpa.repository.JpaRepository

interface CommentJpaRepository : JpaRepository<CommentEntity, Long> {
    fun findAllByRecipeIdOrderByCreatedAtDesc(recipeId: Long): List<CommentEntity>
    fun countByRecipeId(recipeId: Long): Long
}