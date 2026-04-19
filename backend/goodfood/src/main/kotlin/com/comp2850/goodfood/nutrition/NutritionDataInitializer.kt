package com.comp2850.goodfood.nutrition

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class NutritionDataInitializer(
    private val foodNutritionStore: FoodNutritionStore,
    private val nutritionGuideStore: NutritionGuideStore
) : CommandLineRunner {

    override fun run(vararg args: String) {
        seedFoodsIfNeeded()
        seedGuidesIfNeeded()
    }

    private fun seedFoodsIfNeeded() {
        if (foodNutritionStore.count() > 0) return

        listOf(
            FoodNutrition("Apple", 95.0, 0.5, 19.0),
            FoodNutrition("Banana", 105.0, 1.3, 14.0),
            FoodNutrition("Rice", 206.0, 4.3, 0.1),
            FoodNutrition("Milk", 122.0, 8.0, 12.0),
            FoodNutrition("Egg", 78.0, 6.0, 0.6),
            FoodNutrition("Bread", 79.0, 3.0, 1.4)
        ).forEach { foodNutritionStore.save(it) }
    }

    private fun seedGuidesIfNeeded() {
        if (nutritionGuideStore.count() > 0) return

        listOf(
            NutritionGuide("Calories", 500.0, "kcal"),
            NutritionGuide("Protein", 20.0, "g"),
            NutritionGuide("Sugar", 25.0, "g")
        ).forEach { nutritionGuideStore.save(it) }
    }
}