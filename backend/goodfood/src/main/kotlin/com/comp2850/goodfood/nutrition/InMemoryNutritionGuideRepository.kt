package com.comp2850.goodfood.nutrition

import org.springframework.stereotype.Repository

class InMemoryNutritionGuideRepository {

    private val guides = listOf(
        NutritionGuide("Calories", 500.0, "kcal"),
        NutritionGuide("Protein", 20.0, "g"),
        NutritionGuide("Sugar", 25.0, "g")
    )

    fun findByNutrient(nutrient: String): NutritionGuide? {
        return guides.find { it.nutrient.equals(nutrient, ignoreCase = true) }
    }

    fun findAll(): List<NutritionGuide> {
        return guides
    }
}