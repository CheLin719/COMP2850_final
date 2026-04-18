package com.comp2850.goodfood.nutrition

import com.comp2850.goodfood.diary.InMemoryDiaryRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import kotlin.math.round

@Service
class NutritionService(
    private val diaryRepository: InMemoryDiaryRepository,
    private val foodNutritionRepository: InMemoryFoodNutritionRepository,
    private val nutritionGuideRepository: InMemoryNutritionGuideRepository
) {

    fun getAllNutritionGuides(): List<NutritionGuide> {
        return nutritionGuideRepository.findAll()
    }

    fun getAllFoodNutrition(name: String?): List<FoodNutrition> {
        return if (name.isNullOrBlank()) {
            foodNutritionRepository.findAll()
        } else {
            foodNutritionRepository.searchByFoodName(name.trim())
        }
    }

    fun getMyNutritionSummary(authentication: Authentication, date: LocalDate?): NutritionSummary {
        val userEmail = authentication.name

        val entries = if (date == null) {
            diaryRepository.findByUserEmail(userEmail)
        } else {
            diaryRepository.findByUserEmailAndDate(userEmail, date)
        }

        var totalCalories = 0
        var totalProtein = 0.0
        var totalSugar = 0.0
        var matchedEntries = 0
        val unmatchedFoods = mutableListOf<String>()

        for (entry in entries) {
            val nutrition = foodNutritionRepository.findByFoodName(entry.foodName)

            if (nutrition != null) {
                totalCalories += nutrition.calories
                totalProtein += nutrition.protein
                totalSugar += nutrition.sugar
                matchedEntries++
            } else {
                unmatchedFoods.add(entry.foodName)
            }
        }

        return NutritionSummary(
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalSugar = totalSugar,
            matchedEntries = matchedEntries,
            unmatchedFoods = unmatchedFoods.distinct()
        )
    }

    fun getMyNutritionFeedback(authentication: Authentication, days: Int): NutritionFeedback {
        if (days <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "days must be greater than 0")
        }

        val userEmail = authentication.name
        val allEntries = diaryRepository.findByUserEmail(userEmail)

        if (allEntries.isEmpty()) {
            return NutritionFeedback(
                requestedDays = days,
                analysedDays = 0,
                averageDailyCalories = 0.0,
                averageDailyProtein = 0.0,
                averageDailySugar = 0.0,
                messages = listOf("Record at least one meal to receive personalised nutrition feedback."),
                unmatchedFoods = emptyList()
            )
        }

        val latestDate = allEntries.maxOf { it.diaryDate }
        val startDate = latestDate.minusDays(days.toLong() - 1)

        val filteredEntries = allEntries.filter { entry ->
            !entry.diaryDate.isBefore(startDate) && !entry.diaryDate.isAfter(latestDate)
        }

        val groupedByDate = filteredEntries.groupBy { it.diaryDate }.toSortedMap()
        val analysedDays = groupedByDate.size

        var totalCalories = 0
        var totalProtein = 0.0
        var totalSugar = 0.0
        val unmatchedFoods = mutableListOf<String>()

        for ((_, entriesForDate) in groupedByDate) {
            for (entry in entriesForDate) {
                val nutrition = foodNutritionRepository.findByFoodName(entry.foodName)

                if (nutrition != null) {
                    totalCalories += nutrition.calories
                    totalProtein += nutrition.protein
                    totalSugar += nutrition.sugar
                } else {
                    unmatchedFoods.add(entry.foodName)
                }
            }
        }

        val avgCalories = if (analysedDays > 0) round2(totalCalories.toDouble() / analysedDays) else 0.0
        val avgProtein = if (analysedDays > 0) round2(totalProtein / analysedDays) else 0.0
        val avgSugar = if (analysedDays > 0) round2(totalSugar / analysedDays) else 0.0

        val caloriesGuide = getRequiredGuide("Calories")
        val proteinGuide = getRequiredGuide("Protein")
        val sugarGuide = getRequiredGuide("Sugar")

        val messages = mutableListOf<String>()

        if (analysedDays < 3) {
            messages.add("Record at least 3 days of meals to receive more reliable nutrition feedback.")
        }

        if (avgSugar > sugarGuide.dailyTarget) {
            messages.add("Your average sugar intake has been high recently. Try lower-sugar alternatives such as plain milk, fruit, eggs, or less sweet snacks and drinks.")
        }

        if (avgProtein < proteinGuide.dailyTarget * 0.5 && analysedDays > 0) {
            messages.add("Your average protein intake looks low. Try adding foods like eggs, milk, beans, or other protein-rich foods more regularly.")
        }

        if (avgCalories > caloriesGuide.dailyTarget * 1.2) {
            messages.add("Your recent average calorie intake looks high. Check portion size and try to balance heavier meals with lighter choices.")
        }

        if (avgCalories in 0.1..(caloriesGuide.dailyTarget * 0.3)) {
            messages.add("Your recent average calorie intake looks quite low. Make sure you are eating enough balanced meals each day.")
        }

        if (unmatchedFoods.isNotEmpty()) {
            messages.add("Some foods could not be analysed: ${unmatchedFoods.distinct().joinToString(", ")}. Try using simpler or more standard food names.")
        }

        if (messages.isEmpty()) {
            messages.add("Your recent nutrition pattern looks fairly balanced based on the foods we could analyse. Keep maintaining this pattern.")
        }

        return NutritionFeedback(
            requestedDays = days,
            analysedDays = analysedDays,
            averageDailyCalories = avgCalories,
            averageDailyProtein = avgProtein,
            averageDailySugar = avgSugar,
            messages = messages,
            unmatchedFoods = unmatchedFoods.distinct()
        )
    }

    fun getMyNutritionTrends(authentication: Authentication, days: Int): NutritionTrendsResponse {
        if (days <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "days must be greater than 0")
        }

        val userEmail = authentication.name
        val allEntries = diaryRepository.findByUserEmail(userEmail)

        if (allEntries.isEmpty()) {
            return NutritionTrendsResponse(
                requestedDays = days,
                analysedDays = 0,
                enoughData = false,
                message = "Record meals on at least 2 different days to generate nutrition trends.",
                trends = emptyList()
            )
        }

        val latestDate = allEntries.maxOf { it.diaryDate }
        val startDate = latestDate.minusDays(days.toLong() - 1)

        val filteredEntries = allEntries.filter { entry ->
            !entry.diaryDate.isBefore(startDate) && !entry.diaryDate.isAfter(latestDate)
        }

        val groupedByDate = filteredEntries.groupBy { it.diaryDate }

        val trends = groupedByDate
            .map { (date, entriesForDate) ->
                var totalCalories = 0
                var totalProtein = 0.0
                var totalSugar = 0.0

                for (entry in entriesForDate) {
                    val nutrition = foodNutritionRepository.findByFoodName(entry.foodName)
                    if (nutrition != null) {
                        totalCalories += nutrition.calories
                        totalProtein += nutrition.protein
                        totalSugar += nutrition.sugar
                    }
                }

                DailyNutritionTrend(
                    date = date,
                    totalCalories = totalCalories,
                    totalProtein = totalProtein,
                    totalSugar = totalSugar
                )
            }
            .sortedBy { it.date }

        val analysedDays = trends.size
        val enoughData = analysedDays >= 2
        val message = if (enoughData) {
            null
        } else {
            "Record meals on at least 2 different days to generate nutrition trends."
        }

        return NutritionTrendsResponse(
            requestedDays = days,
            analysedDays = analysedDays,
            enoughData = enoughData,
            message = message,
            trends = trends
        )
    }

    fun getDailyNutritionStatus(authentication: Authentication, date: LocalDate?): DailyNutritionStatusResponse {
        val targetDate = date ?: LocalDate.now()
        val summary = getMyNutritionSummary(authentication, targetDate)

        val caloriesGuide = getRequiredGuide("Calories")
        val proteinGuide = getRequiredGuide("Protein")
        val sugarGuide = getRequiredGuide("Sugar")

        val hasData = summary.matchedEntries > 0 || summary.unmatchedFoods.isNotEmpty()

        if (!hasData) {
            return DailyNutritionStatusResponse(
                date = targetDate,
                hasData = false,
                items = listOf(
                    NutritionStatusItem(
                        nutrient = caloriesGuide.nutrient,
                        currentValue = 0.0,
                        targetValue = caloriesGuide.dailyTarget,
                        unit = caloriesGuide.unit,
                        status = NutritionStatusLevel.GRAY,
                        message = "No meals recorded today yet."
                    ),
                    NutritionStatusItem(
                        nutrient = proteinGuide.nutrient,
                        currentValue = 0.0,
                        targetValue = proteinGuide.dailyTarget,
                        unit = proteinGuide.unit,
                        status = NutritionStatusLevel.GRAY,
                        message = "No meals recorded today yet."
                    ),
                    NutritionStatusItem(
                        nutrient = sugarGuide.nutrient,
                        currentValue = 0.0,
                        targetValue = sugarGuide.dailyTarget,
                        unit = sugarGuide.unit,
                        status = NutritionStatusLevel.GRAY,
                        message = "No meals recorded today yet."
                    )
                )
            )
        }

        return DailyNutritionStatusResponse(
            date = targetDate,
            hasData = true,
            items = listOf(
                buildStatusItem(
                    nutrient = caloriesGuide.nutrient,
                    currentValue = summary.totalCalories.toDouble(),
                    targetValue = caloriesGuide.dailyTarget,
                    unit = caloriesGuide.unit,
                    higherIsWorse = true,
                    lowMessage = "Your calorie intake is still low today.",
                    nearMessage = "You are getting close to your daily demo target for calories.",
                    goodMessage = "Your calorie intake is around the demo target.",
                    highMessage = "Your calorie intake is above the demo target today."
                ),
                buildStatusItem(
                    nutrient = proteinGuide.nutrient,
                    currentValue = summary.totalProtein,
                    targetValue = proteinGuide.dailyTarget,
                    unit = proteinGuide.unit,
                    higherIsWorse = false,
                    lowMessage = "Your protein intake is still low today.",
                    nearMessage = "You are getting close to your protein target today.",
                    goodMessage = "Your protein intake looks good today.",
                    highMessage = "Your protein intake is above the demo target today."
                ),
                buildStatusItem(
                    nutrient = sugarGuide.nutrient,
                    currentValue = summary.totalSugar,
                    targetValue = sugarGuide.dailyTarget,
                    unit = sugarGuide.unit,
                    higherIsWorse = true,
                    lowMessage = "Your sugar intake is low today.",
                    nearMessage = "Your sugar intake is getting close to the limit.",
                    goodMessage = "Your sugar intake is within the demo target.",
                    highMessage = "Your sugar intake is above the demo target today."
                )
            )
        )
    }

    private fun getRequiredGuide(nutrient: String): NutritionGuide {
        return nutritionGuideRepository.findByNutrient(nutrient)
            ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "$nutrient guide not found"
            )
    }

    private fun buildStatusItem(
        nutrient: String,
        currentValue: Double,
        targetValue: Double,
        unit: String,
        higherIsWorse: Boolean,
        lowMessage: String,
        nearMessage: String,
        goodMessage: String,
        highMessage: String
    ): NutritionStatusItem {
        val ratio = if (targetValue == 0.0) 0.0 else currentValue / targetValue

        val status = if (higherIsWorse) {
            when {
                ratio < 0.8 -> NutritionStatusLevel.GREEN
                ratio <= 1.0 -> NutritionStatusLevel.YELLOW
                else -> NutritionStatusLevel.RED
            }
        } else {
            when {
                ratio < 0.5 -> NutritionStatusLevel.RED
                ratio < 0.8 -> NutritionStatusLevel.YELLOW
                else -> NutritionStatusLevel.GREEN
            }
        }

        val message = if (higherIsWorse) {
            when (status) {
                NutritionStatusLevel.GREEN -> lowMessage
                NutritionStatusLevel.YELLOW -> nearMessage
                NutritionStatusLevel.RED -> highMessage
                NutritionStatusLevel.GRAY -> "No data."
            }
        } else {
            when (status) {
                NutritionStatusLevel.RED -> lowMessage
                NutritionStatusLevel.YELLOW -> nearMessage
                NutritionStatusLevel.GREEN -> goodMessage
                NutritionStatusLevel.GRAY -> "No data."
            }
        }

        return NutritionStatusItem(
            nutrient = nutrient,
            currentValue = round2(currentValue),
            targetValue = targetValue,
            unit = unit,
            status = status,
            message = message
        )
    }

    private fun round2(value: Double): Double {
        return round(value * 100) / 100
    }

    fun getFoodSuggestions(name: String?): List<String> {
        if (name.isNullOrBlank()) {
            return emptyList()
        }
        return foodNutritionRepository.suggestFoodNames(name.trim())
    }
}