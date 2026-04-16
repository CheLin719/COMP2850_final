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
    ): List<DailyNutritionTrend> {
        return nutritionService.getMyNutritionTrends(authentication, days)
    }
}