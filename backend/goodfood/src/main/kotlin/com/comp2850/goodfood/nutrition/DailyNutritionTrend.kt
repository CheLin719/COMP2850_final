package com.comp2850.goodfood.nutrition

import java.time.LocalDate

data class DailyNutritionTrend(
    val date: LocalDate,
    val totalCalories: Int,
    val totalProtein: Double,
    val totalSugar: Double
)