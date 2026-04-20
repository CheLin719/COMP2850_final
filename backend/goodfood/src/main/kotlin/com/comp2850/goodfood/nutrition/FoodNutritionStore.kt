package com.comp2850.goodfood.nutrition

interface FoodNutritionStore {
    fun findByFoodName(foodName: String): FoodNutrition?
    fun findAll(): List<FoodNutrition>
    fun searchByFoodName(name: String): List<FoodNutrition>
    fun suggestFoodNames(name: String): List<String>
    fun save(foodNutrition: FoodNutrition): FoodNutrition
    fun count(): Long
}