package com.comp2850.goodfood.nutrition

import org.springframework.data.jpa.repository.JpaRepository

interface FoodNutritionJpaRepository : JpaRepository<FoodNutritionEntity, Long> {
    fun findByFoodNameIgnoreCase(foodName: String): FoodNutritionEntity?
    fun findAllByFoodNameContainingIgnoreCaseOrderByFoodNameAsc(name: String): List<FoodNutritionEntity>
}