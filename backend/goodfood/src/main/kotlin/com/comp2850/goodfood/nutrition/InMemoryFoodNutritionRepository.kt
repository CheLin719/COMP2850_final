package com.comp2850.goodfood.nutrition

class InMemoryFoodNutritionRepository {

    private val foods = listOf(
        FoodNutrition("Apple", 95.0, 0.5, 19.0),
        FoodNutrition("Banana", 105.0, 1.3, 14.0),
        FoodNutrition("Rice", 206.0, 4.3, 0.1),
        FoodNutrition("Milk", 122.0, 8.0, 12.0),
        FoodNutrition("Egg", 78.0, 6.0, 0.6),
        FoodNutrition("Bread", 79.0, 3.0, 1.4)
    )

    fun findByFoodName(foodName: String): FoodNutrition? {
        return foods.find { it.foodName.equals(foodName, ignoreCase = true) }
    }

    fun findAll(): List<FoodNutrition> {
        return foods.sortedBy { it.foodName }
    }

    fun searchByFoodName(name: String): List<FoodNutrition> {
        return foods
            .filter { it.foodName.contains(name, ignoreCase = true) }
            .sortedBy { it.foodName }
    }

    fun suggestFoodNames(name: String): List<String> {
        return foods
            .map { it.foodName }
            .filter { it.contains(name, ignoreCase = true) }
            .sorted()
    }
}