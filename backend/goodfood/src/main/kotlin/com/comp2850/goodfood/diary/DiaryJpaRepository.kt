package com.comp2850.goodfood.diary

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface DiaryJpaRepository : JpaRepository<DiaryEntryEntity, Long> {
    fun findAllByUserEmail(userEmail: String): List<DiaryEntryEntity>
    fun findAllByUserEmailAndDiaryDate(userEmail: String, diaryDate: LocalDate): List<DiaryEntryEntity>
}