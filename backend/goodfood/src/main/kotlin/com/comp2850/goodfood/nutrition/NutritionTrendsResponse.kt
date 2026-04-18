package com.comp2850.goodfood.nutrition

data class NutritionTrendsResponse(
    val requestedDays: Int,
    val analysedDays: Int,
    val enoughData: Boolean,
    val message: String?,
    val trends: List<DailyNutritionTrend>
)