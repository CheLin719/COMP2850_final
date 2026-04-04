package com.comp2850.goodfood.diary

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class UpdateDiaryRequest(
    @field:NotBlank(message = "foodName cannot be blank")
    val foodName: String,

    @field:NotBlank(message = "quantity cannot be blank")
    val quantity: String,

    @field:NotNull(message = "mealType is required")
    val mealType: MealType?,

    @field:NotNull(message = "diaryDate is required")
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val diaryDate: LocalDate?
)