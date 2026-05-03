package com.comp2850.goodfood.nutrition

import com.comp2850.goodfood.diary.DiaryEntry
import com.comp2850.goodfood.diary.DiaryStore
import com.comp2850.goodfood.diary.MealType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

class NutritionServiceUnitTest {

    private fun auth(email: String = "user@test.com"): Authentication {
        return UsernamePasswordAuthenticationToken(email, null)
    }

    private fun entry(
        foodName: String,
        date: LocalDate = LocalDate.of(2030, 1, 1),
        servings: Double = 1.0
    ): DiaryEntry {
        return DiaryEntry(
            id = 1L,
            userEmail = "user@test.com",
            foodName = foodName,
            quantity = "1 serving",
            servings = servings,
            mealType = MealType.BREAKFAST,
            diaryDate = date
        )
    }

    private fun service(
        diaryEntries: List<DiaryEntry> = emptyList(),
        foods: List<FoodNutrition> = listOf(
            FoodNutrition("Apple", 100.0, 1.0, 10.0),
            FoodNutrition("Chicken", 250.0, 30.0, 0.0),
            FoodNutrition("Candy", 600.0, 2.0, 80.0)
        ),
        guides: List<NutritionGuide> = listOf(
            NutritionGuide("Calories", 2000.0, "kcal"),
            NutritionGuide("Protein", 100.0, "g"),
            NutritionGuide("Sugar", 50.0, "g")
        )
    ): NutritionService {
        return NutritionService(
            diaryRepository = FakeDiaryStore(diaryEntries.toMutableList()),
            foodNutritionRepository = FakeFoodNutritionStore(foods.toMutableList()),
            nutritionGuideRepository = FakeNutritionGuideStore(guides.toMutableList())
        )
    }

    @Test
    fun `blank food search returns all foods`() {
        val foodStore = FakeFoodNutritionStore(
            mutableListOf(
                FoodNutrition("Apple", 100.0, 1.0, 10.0),
                FoodNutrition("Chicken", 250.0, 30.0, 0.0)
            )
        )

        val nutritionService = NutritionService(
            diaryRepository = FakeDiaryStore(),
            foodNutritionRepository = foodStore,
            nutritionGuideRepository = FakeNutritionGuideStore()
        )

        val result = nutritionService.getAllFoodNutrition("   ")

        assertEquals(2, result.size)
        assertNull(foodStore.lastSearchName)
    }

    @Test
    fun `named food search trims input before searching`() {
        val foodStore = FakeFoodNutritionStore(
            mutableListOf(
                FoodNutrition("Apple", 100.0, 1.0, 10.0),
                FoodNutrition("Chicken", 250.0, 30.0, 0.0)
            )
        )

        val nutritionService = NutritionService(
            diaryRepository = FakeDiaryStore(),
            foodNutritionRepository = foodStore,
            nutritionGuideRepository = FakeNutritionGuideStore()
        )

        val result = nutritionService.getAllFoodNutrition(" apple ")

        assertEquals("apple", foodStore.lastSearchName)
        assertEquals(1, result.size)
        assertEquals("Apple", result.first().foodName)
    }

    @Test
    fun `blank food suggestions returns empty list`() {
        val result = service().getFoodSuggestions("   ")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `nutrition summary counts matched and unmatched foods`() {
        val nutritionService = service(
            diaryEntries = listOf(
                entry("Apple", servings = 2.0),
                entry("Unknown Food"),
                entry("Unknown Food")
            )
        )

        val summary = nutritionService.getMyNutritionSummary(auth(), null)

        assertEquals(200.0, summary.totalCalories)
        assertEquals(2.0, summary.totalProtein)
        assertEquals(20.0, summary.totalSugar)
        assertEquals(1, summary.matchedEntries)
        assertEquals(listOf("Unknown Food"), summary.unmatchedFoods)
    }

    @Test
    fun `nutrition feedback rejects invalid day count`() {
        val exception = assertThrows(ResponseStatusException::class.java) {
            service().getMyNutritionFeedback(auth(), 0)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun `nutrition feedback returns no data message when diary is empty`() {
        val feedback = service(diaryEntries = emptyList())
            .getMyNutritionFeedback(auth(), 7)

        assertEquals(7, feedback.requestedDays)
        assertEquals(0, feedback.analysedDays)
        assertEquals(0.0, feedback.averageDailyCalories)
        assertTrue(feedback.messages.first().contains("Record at least one meal"))
    }

    @Test
    fun `nutrition feedback reports high sugar low protein high calories and unmatched foods`() {
        val nutritionService = service(
            diaryEntries = listOf(
                entry("Candy", date = LocalDate.of(2030, 1, 1), servings = 5.0),
                entry("Unknown Food", date = LocalDate.of(2030, 1, 1))
            )
        )

        val feedback = nutritionService.getMyNutritionFeedback(auth(), 2)

        assertEquals(2, feedback.requestedDays)
        assertEquals(1, feedback.analysedDays)
        assertTrue(feedback.averageDailyCalories > 2000.0)
        assertTrue(feedback.messages.any { it.contains("at least 3 days") })
        assertTrue(feedback.messages.any { it.contains("sugar intake has been high") })
        assertTrue(feedback.messages.any { it.contains("protein intake looks low") })
        assertTrue(feedback.messages.any { it.contains("calorie intake looks high") })
        assertTrue(feedback.messages.any { it.contains("could not be analysed") })
        assertEquals(listOf("Unknown Food"), feedback.unmatchedFoods)
    }

    @Test
    fun `nutrition trends rejects invalid day count`() {
        val exception = assertThrows(ResponseStatusException::class.java) {
            service().getMyNutritionTrends(auth(), -1)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun `nutrition trends returns not enough data when diary is empty`() {
        val trends = service(diaryEntries = emptyList())
            .getMyNutritionTrends(auth(), 7)

        assertEquals(7, trends.requestedDays)
        assertEquals(0, trends.analysedDays)
        assertFalse(trends.enoughData)
        assertTrue(trends.trends.isEmpty())
        assertTrue(trends.message!!.contains("at least 2 different days"))
    }

    @Test
    fun `nutrition trends returns enough data for two diary dates`() {
        val nutritionService = service(
            diaryEntries = listOf(
                entry("Apple", date = LocalDate.of(2030, 1, 1)),
                entry("Chicken", date = LocalDate.of(2030, 1, 2))
            )
        )

        val trends = nutritionService.getMyNutritionTrends(auth(), 7)

        assertEquals(2, trends.analysedDays)
        assertTrue(trends.enoughData)
        assertNull(trends.message)
        assertEquals(LocalDate.of(2030, 1, 1), trends.trends[0].date)
        assertEquals(LocalDate.of(2030, 1, 2), trends.trends[1].date)
    }

    @Test
    fun `daily nutrition status returns gray items when no data exists`() {
        val status = service(diaryEntries = emptyList())
            .getDailyNutritionStatus(auth(), LocalDate.of(2030, 1, 1))

        assertFalse(status.hasData)
        assertEquals(3, status.items.size)
        assertTrue(status.items.all { it.status == NutritionStatusLevel.GRAY })
        assertTrue(status.items.all { it.currentValue == 0.0 })
    }

    @Test
    fun `daily nutrition status returns red items for high calories high sugar and low protein`() {
        val nutritionService = service(
            diaryEntries = listOf(
                entry("Candy", date = LocalDate.of(2030, 1, 1), servings = 4.0)
            )
        )

        val status = nutritionService.getDailyNutritionStatus(auth(), LocalDate.of(2030, 1, 1))

        assertTrue(status.hasData)
        assertEquals(NutritionStatusLevel.RED, status.items.first { it.nutrient == "Calories" }.status)
        assertEquals(NutritionStatusLevel.RED, status.items.first { it.nutrient == "Protein" }.status)
        assertEquals(NutritionStatusLevel.RED, status.items.first { it.nutrient == "Sugar" }.status)
    }

    @Test
    fun `daily nutrition status returns mixed green and yellow statuses`() {
        val nutritionService = service(
            diaryEntries = listOf(
                entry("Chicken", date = LocalDate.of(2030, 1, 1), servings = 2.0)
            )
        )

        val status = nutritionService.getDailyNutritionStatus(auth(), LocalDate.of(2030, 1, 1))

        assertTrue(status.hasData)
        assertEquals(NutritionStatusLevel.GREEN, status.items.first { it.nutrient == "Calories" }.status)
        assertEquals(NutritionStatusLevel.YELLOW, status.items.first { it.nutrient == "Protein" }.status)
        assertEquals(NutritionStatusLevel.GREEN, status.items.first { it.nutrient == "Sugar" }.status)
    }

    private class FakeDiaryStore(
        private val entries: MutableList<DiaryEntry> = mutableListOf()
    ) : DiaryStore {
        override fun save(entry: DiaryEntry): DiaryEntry {
            entries.add(entry)
            return entry
        }

        override fun update(entry: DiaryEntry): DiaryEntry {
            entries.removeIf { it.id == entry.id }
            entries.add(entry)
            return entry
        }

        override fun findByUserEmail(userEmail: String): List<DiaryEntry> {
            return entries.filter { it.userEmail == userEmail }
        }

        override fun findByUserEmailAndDate(userEmail: String, diaryDate: LocalDate): List<DiaryEntry> {
            return entries.filter { it.userEmail == userEmail && it.diaryDate == diaryDate }
        }

        override fun findById(id: Long): DiaryEntry? {
            return entries.find { it.id == id }
        }

        override fun delete(entry: DiaryEntry) {
            entries.removeIf { it.id == entry.id }
        }
    }

    private class FakeFoodNutritionStore(
        private val foods: MutableList<FoodNutrition> = mutableListOf(
            FoodNutrition("Apple", 100.0, 1.0, 10.0),
            FoodNutrition("Chicken", 250.0, 30.0, 0.0),
            FoodNutrition("Candy", 600.0, 2.0, 80.0)
        )
    ) : FoodNutritionStore {
        var lastSearchName: String? = null

        override fun findByFoodName(foodName: String): FoodNutrition? {
            return foods.find { it.foodName.equals(foodName, ignoreCase = true) }
        }

        override fun findAll(): List<FoodNutrition> {
            return foods.toList()
        }

        override fun searchByFoodName(name: String): List<FoodNutrition> {
            lastSearchName = name
            return foods.filter { it.foodName.contains(name, ignoreCase = true) }
        }

        override fun suggestFoodNames(name: String): List<String> {
            return foods
                .filter { it.foodName.contains(name, ignoreCase = true) }
                .map { it.foodName }
        }

        override fun save(foodNutrition: FoodNutrition): FoodNutrition {
            foods.add(foodNutrition)
            return foodNutrition
        }

        override fun count(): Long {
            return foods.size.toLong()
        }
    }

    private class FakeNutritionGuideStore(
        private val guides: MutableList<NutritionGuide> = mutableListOf(
            NutritionGuide("Calories", 2000.0, "kcal"),
            NutritionGuide("Protein", 100.0, "g"),
            NutritionGuide("Sugar", 50.0, "g")
        )
    ) : NutritionGuideStore {
        override fun findByNutrient(nutrient: String): NutritionGuide? {
            return guides.find { it.nutrient.equals(nutrient, ignoreCase = true) }
        }

        override fun findAll(): List<NutritionGuide> {
            return guides.toList()
        }

        override fun save(guide: NutritionGuide): NutritionGuide {
            guides.add(guide)
            return guide
        }

        override fun count(): Long {
            return guides.size.toLong()
        }
    }
}
