package com.comp2850.goodfood.nutrition

import org.springframework.data.jpa.repository.JpaRepository

interface NutritionGuideJpaRepository : JpaRepository<NutritionGuideEntity, Long> {
    fun findByNutrientIgnoreCase(nutrient: String): NutritionGuideEntity?
}