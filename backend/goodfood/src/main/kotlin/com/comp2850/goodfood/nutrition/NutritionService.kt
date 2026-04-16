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
    private val foodNutritionRepository: InMemoryFoodNutritionRepository
) {

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

    fun getMyNutritionTrends(authentication: Authentication, days: Int): List<DailyNutritionTrend> {
        if (days <= 0) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "days must be greater than 0")
        }

        val userEmail = authentication.name
        val allEntries = diaryRepository.findByUserEmail(userEmail)

        if (allEntries.isEmpty()) {
            return emptyList()
        }

        val latestDate = allEntries.maxOf { it.diaryDate }
        val startDate = latestDate.minusDays(days.toLong() - 1)

        val filteredEntries = allEntries.filter { entry ->
            !entry.diaryDate.isBefore(startDate) && !entry.diaryDate.isAfter(latestDate)
        }

        val groupedByDate = filteredEntries.groupBy { it.diaryDate }

        return groupedByDate
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

        val messages = mutableListOf<String>()

        if (analysedDays < 3) {
            messages.add("Record at least 3 days of meals to receive more reliable nutrition feedback.")
        }

        if (avgSugar > 25.0) {
            messages.add("Your average sugar intake has been high recently. Try lower-sugar alternatives such as plain milk, fruit, eggs, or less sweet snacks and drinks.")
        }

        if (avgProtein < 10.0 && analysedDays > 0) {
            messages.add("Your average protein intake looks low. Try adding foods like eggs, milk, beans, or other protein-rich foods more regularly.")
        }

        if (avgCalories > 600.0) {
            messages.add("Your recent average calorie intake looks high. Check portion size and try to balance heavier meals with lighter choices.")
        }

        if (avgCalories in 0.1..149.99) {
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

    private fun round2(value: Double): Double {
        return round(value * 100) / 100
    }
}