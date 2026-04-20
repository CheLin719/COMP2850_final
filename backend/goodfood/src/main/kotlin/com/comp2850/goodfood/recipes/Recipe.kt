package com.comp2850.goodfood.recipes

data class Recipe(
    val id: Long,
    val name: String,
    val emoji: String?,
    val tag: String?,
    val kcal: Int?,
    val cost: String?,
    val timeMin: Int?,
    val ingredients: List<String>,
    val steps: List<String>
)