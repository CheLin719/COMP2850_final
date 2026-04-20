package com.comp2850.goodfood.nutrition

data class NutritionStatusItem(
    val nutrient: String,
    val currentValue: Double,
    val targetValue: Double,
    val unit: String,
    val status: NutritionStatusLevel,
    val message: String
)