package com.comp2850.goodfood.nutrition

import java.time.LocalDate

data class DailyNutritionTrend(
    val date: LocalDate,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalSugar: Double
)