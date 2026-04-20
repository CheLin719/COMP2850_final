package com.comp2850.goodfood.nutrition

import org.springframework.stereotype.Repository

@Repository
class NutritionGuideDatabaseRepository(
    private val nutritionGuideJpaRepository: NutritionGuideJpaRepository
) : NutritionGuideStore {

    override fun findByNutrient(nutrient: String): NutritionGuide? {
        return nutritionGuideJpaRepository.findByNutrientIgnoreCase(nutrient)?.toModel()
    }

    override fun findAll(): List<NutritionGuide> {
        return nutritionGuideJpaRepository.findAll()
            .map { it.toModel() }
            .sortedBy { it.nutrient }
    }

    override fun save(guide: NutritionGuide): NutritionGuide {
        val existing = nutritionGuideJpaRepository.findByNutrientIgnoreCase(guide.nutrient)

        val entity = if (existing != null) {
            existing.apply {
                nutrient = guide.nutrient
                dailyTarget = guide.dailyTarget
                unit = guide.unit
            }
        } else {
            NutritionGuideEntity(
                nutrient = guide.nutrient,
                dailyTarget = guide.dailyTarget,
                unit = guide.unit
            )
        }

        return nutritionGuideJpaRepository.save(entity).toModel()
    }

    override fun count(): Long {
        return nutritionGuideJpaRepository.count()
    }

    private fun NutritionGuideEntity.toModel(): NutritionGuide {
        return NutritionGuide(
            nutrient = this.nutrient,
            dailyTarget = this.dailyTarget,
            unit = this.unit
        )
    }
}