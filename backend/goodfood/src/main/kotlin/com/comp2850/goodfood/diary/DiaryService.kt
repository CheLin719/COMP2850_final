package com.comp2850.goodfood.diary

import com.comp2850.goodfood.nutrition.InMemoryFoodNutritionRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class DiaryService(
    private val diaryRepository: InMemoryDiaryRepository,
    private val foodNutritionRepository: InMemoryFoodNutritionRepository
) {

    fun createDiary(request: CreateDiaryRequest, authentication: Authentication): DiarySaveResponse {
        val userEmail = authentication.name

        val entry = DiaryEntry(
            id = diaryRepository.nextId(),
            userEmail = userEmail,
            foodName = request.foodName,
            quantity = request.quantity,
            mealType = request.mealType!!,
            diaryDate = request.diaryDate!!
        )

        val savedEntry = diaryRepository.save(entry)
        return buildDiarySaveResponse(savedEntry)
    }

    fun getMyDiaryEntries(authentication: Authentication, date: LocalDate?): List<DiaryEntry> {
        val userEmail = authentication.name

        val entries = if (date == null) {
            diaryRepository.findByUserEmail(userEmail)
        } else {
            diaryRepository.findByUserEmailAndDate(userEmail, date)
        }

        return entries.sortedWith(
            compareByDescending<DiaryEntry> { it.diaryDate }
                .thenBy { it.mealType.ordinal }
        )
    }

    fun deleteMyDiaryEntry(id: Long, authentication: Authentication) {
        val userEmail = authentication.name
        val entry = diaryRepository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "diary entry not found")

        if (entry.userEmail != userEmail) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "you cannot delete this diary entry")
        }

        diaryRepository.delete(entry)
    }

    fun updateMyDiaryEntry(
        id: Long,
        request: UpdateDiaryRequest,
        authentication: Authentication
    ): DiarySaveResponse {
        val userEmail = authentication.name
        val existingEntry = diaryRepository.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "diary entry not found")

        if (existingEntry.userEmail != userEmail) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "you cannot update this diary entry")
        }

        val updatedEntry = existingEntry.copy(
            foodName = request.foodName,
            quantity = request.quantity,
            mealType = request.mealType!!,
            diaryDate = request.diaryDate!!
        )

        val savedEntry = diaryRepository.update(updatedEntry)
        return buildDiarySaveResponse(savedEntry)
    }

    private fun buildDiarySaveResponse(entry: DiaryEntry): DiarySaveResponse {
        val nutritionMatched = foodNutritionRepository.findByFoodName(entry.foodName) != null

        val message = if (nutritionMatched) {
            null
        } else {
            "Diary entry saved, but this food is not in the nutrition dataset yet. It may not appear fully in nutrition summary, feedback, or trends."
        }

        return DiarySaveResponse(
            entry = entry,
            nutritionMatched = nutritionMatched,
            message = message
        )
    }
}