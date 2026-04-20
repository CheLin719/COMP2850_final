package com.comp2850.goodfood.nutrition

interface NutritionGuideStore {
    fun findByNutrient(nutrient: String): NutritionGuide?
    fun findAll(): List<NutritionGuide>
    fun save(guide: NutritionGuide): NutritionGuide
    fun count(): Long
}