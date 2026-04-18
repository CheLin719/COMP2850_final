package com.comp2850.goodfood.diary

data class DiarySaveResponse(
    val entry: DiaryEntry,
    val nutritionMatched: Boolean,
    val message: String?
)