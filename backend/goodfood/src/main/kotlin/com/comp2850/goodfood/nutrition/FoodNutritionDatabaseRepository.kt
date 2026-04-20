package com.comp2850.goodfood.nutrition

import org.springframework.stereotype.Repository

@Repository
class FoodNutritionDatabaseRepository(
    private val foodNutritionJpaRepository: FoodNutritionJpaRepository
) : FoodNutritionStore {

    override fun findByFoodName(foodName: String): FoodNutrition? {
        return foodNutritionJpaRepository.findByFoodNameIgnoreCase(foodName)?.toModel()
    }

    override fun findAll(): List<FoodNutrition> {
        return foodNutritionJpaRepository.findAll()
            .map { it.toModel() }
            .sortedBy { it.foodName }
    }

    override fun searchByFoodName(name: String): List<FoodNutrition> {
        return foodNutritionJpaRepository.findAllByFoodNameContainingIgnoreCaseOrderByFoodNameAsc(name)
            .map { it.toModel() }
    }

    override fun suggestFoodNames(name: String): List<String> {
        return foodNutritionJpaRepository.findAllByFoodNameContainingIgnoreCaseOrderByFoodNameAsc(name)
            .map { it.foodName }
    }

    override fun save(foodNutrition: FoodNutrition): FoodNutrition {
        val existing = foodNutritionJpaRepository.findByFoodNameIgnoreCase(foodNutrition.foodName)

        val entity = if (existing != null) {
            existing.apply {
                foodName = foodNutrition.foodName
                calories = foodNutrition.calories
                protein = foodNutrition.protein
                sugar = foodNutrition.sugar
            }
        } else {
            FoodNutritionEntity(
                foodName = foodNutrition.foodName,
                calories = foodNutrition.calories,
                protein = foodNutrition.protein,
                sugar = foodNutrition.sugar
            )
        }

        return foodNutritionJpaRepository.save(entity).toModel()
    }

    override fun count(): Long {
        return foodNutritionJpaRepository.count()
    }

    private fun FoodNutritionEntity.toModel(): FoodNutrition {
        return FoodNutrition(
            foodName = this.foodName,
            calories = this.calories,
            protein = this.protein,
            sugar = this.sugar
        )
    }
}