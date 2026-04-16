package com.comp2850.goodfood.nutrition

data class NutritionFeedback(
    val requestedDays: Int,
    val analysedDays: Int,
    val averageDailyCalories: Double,
    val averageDailyProtein: Double,
    val averageDailySugar: Double,
    val messages: List<String>,
    val unmatchedFoods: List<String>
)