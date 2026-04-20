package com.comp2850.goodfood.exercise

import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ExerciseJpaRepository : JpaRepository<ExerciseEntity, Long> {
    fun findAllByUserIdOrderByDateDescCreatedAtDesc(userId: String): List<ExerciseEntity>
    fun findAllByUserIdAndDateOrderByCreatedAtDesc(userId: String, date: LocalDate): List<ExerciseEntity>
    fun findAllByUserIdAndDateBetweenOrderByDateDescCreatedAtDesc(
        userId: String,
        from: LocalDate,
        to: LocalDate
    ): List<ExerciseEntity>
}