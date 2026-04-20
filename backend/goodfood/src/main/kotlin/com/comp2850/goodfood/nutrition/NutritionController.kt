package com.comp2850.goodfood.nutrition

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/nutrition")
class NutritionController(
    private val nutritionService: NutritionService
) {

    @GetMapping("/guides")
    fun getAllNutritionGuides(): List<NutritionGuide> {
        return nutritionService.getAllNutritionGuides()
    }

    @GetMapping("/foods")
    fun getAllFoodNutrition(
        @RequestParam(required = false) name: String?
    ): List<FoodNutrition> {
        return nutritionService.getAllFoodNutrition(name)
    }

    @GetMapping("/foods/suggestions")
    fun getFoodSuggestions(
        @RequestParam(required = false) name: String?
    ): List<String> {
        return nutritionService.getFoodSuggestions(name)
    }

    @GetMapping("/summary")
    fun getMyNutritionSummary(
        authentication: Authentication,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        date: LocalDate?
    ): NutritionSummary {
        return nutritionService.getMyNutritionSummary(authentication, date)
    }

    @GetMapping("/feedback")
    fun getMyNutritionFeedback(
        authentication: Authentication,
        @RequestParam(required = false, defaultValue = "5") days: Int
    ): NutritionFeedback {
        return nutritionService.getMyNutritionFeedback(authentication, days)
    }

    @GetMapping("/trends")
    fun getMyNutritionTrends(
        authentication: Authentication,
        @RequestParam(required = false, defaultValue = "7") days: Int
    ): NutritionTrendsResponse {
        return nutritionService.getMyNutritionTrends(authentication, days)
    }

    @GetMapping("/status")
    fun getDailyNutritionStatus(
        authentication: Authentication,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        date: LocalDate?
    ): DailyNutritionStatusResponse {
        return nutritionService.getDailyNutritionStatus(authentication, date)
    }

    @GetMapping("/status/today")
    fun getTodayNutritionStatus(authentication: Authentication): DailyNutritionStatusResponse {
        return nutritionService.getDailyNutritionStatus(authentication, LocalDate.now())
    }
}