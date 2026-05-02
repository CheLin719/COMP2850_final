package com.comp2850.goodfood.validation

import com.comp2850.goodfood.appointments.ApiAppointmentCreateRequest
import com.comp2850.goodfood.appointments.ApiAppointmentStatusUpdateRequest
import com.comp2850.goodfood.exercise.ApiExerciseCreateRequest
import com.comp2850.goodfood.plans.ApiPlanSaveRequest
import com.comp2850.goodfood.recipes.ApiRecipeCreateRequest
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class RequestValidationUnitTest {

    private val validator: Validator = Validation.buildDefaultValidatorFactory().validator

    private fun violationFields(target: Any): Set<String> {
        return validator.validate(target)
            .map { it.propertyPath.toString() }
            .toSet()
    }

    @Test
    fun `valid exercise request has no validation errors`() {
        val request = ApiExerciseCreateRequest(
            date = LocalDate.now(),
            activity = "Running",
            duration = 30,
            kcal = 250
        )

        assertTrue(violationFields(request).isEmpty())
    }

    @Test
    fun `exercise request rejects blank activity negative duration and negative kcal`() {
        val request = ApiExerciseCreateRequest(
            date = LocalDate.now(),
            activity = "",
            duration = 0,
            kcal = 0
        )

        val fields = violationFields(request)

        assertTrue(fields.contains("activity"))
        assertTrue(fields.contains("duration"))
        assertTrue(fields.contains("kcal"))
    }

    @Test
    fun `valid recipe request has no validation errors`() {
        val request = ApiRecipeCreateRequest(
            name = "Oats",
            emoji = "🥣",
            tag = "Breakfast",
            kcal = 350,
            cost = "Low",
            timeMin = 10,
            ingredients = listOf("Oats", "Milk"),
            steps = listOf("Mix", "Serve")
        )

        assertTrue(violationFields(request).isEmpty())
    }

    @Test
    fun `recipe request rejects blank name empty ingredients and empty steps`() {
        val request = ApiRecipeCreateRequest(
            name = "",
            ingredients = emptyList(),
            steps = emptyList()
        )

        val fields = violationFields(request)

        assertTrue(fields.contains("name"))
        assertTrue(fields.contains("ingredients"))
        assertTrue(fields.contains("steps"))
    }

    @Test
    fun `valid plan request has no validation errors`() {
        val request = ApiPlanSaveRequest(
            planType = "meal",
            daysJson = """{"monday":[]}""",
            targetKcal = 2000,
            targetProtein = 120,
            targetCarbsPct = 50,
            targetFatPct = 25,
            notes = "Test plan"
        )

        assertTrue(violationFields(request).isEmpty())
    }

    @Test
    fun `plan request rejects blank plan type and blank daysJson`() {
        val request = ApiPlanSaveRequest(
            planType = "",
            daysJson = ""
        )

        val fields = violationFields(request)

        assertTrue(fields.contains("planType"))
        assertTrue(fields.contains("daysJson"))
    }

    @Test
    fun `appointment request rejects blank client id`() {
        val request = ApiAppointmentCreateRequest(
            clientId = "",
            date = LocalDate.now().plusDays(1),
            time = LocalTime.of(10, 0),
            type = "Consultation"
        )

        val fields = violationFields(request)

        assertTrue(fields.contains("clientId"))
    }

    @Test
    fun `appointment status update rejects blank status`() {
        val request = ApiAppointmentStatusUpdateRequest(
            status = ""
        )

        val fields = violationFields(request)

        assertTrue(fields.contains("status"))
    }
}
