package com.comp2850.goodfood.nutrition

import java.time.LocalDate

data class DailyNutritionStatusResponse(
    val date: LocalDate,
    val hasData: Boolean,
    val items: List<NutritionStatusItem>
)