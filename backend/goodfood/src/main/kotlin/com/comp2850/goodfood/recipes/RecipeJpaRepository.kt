package com.comp2850.goodfood.recipes

import org.springframework.data.jpa.repository.JpaRepository

interface RecipeJpaRepository : JpaRepository<RecipeEntity, Long>