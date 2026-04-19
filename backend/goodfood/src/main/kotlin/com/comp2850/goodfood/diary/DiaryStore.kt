package com.comp2850.goodfood.diary

import java.time.LocalDate

interface DiaryStore {
    fun save(entry: DiaryEntry): DiaryEntry
    fun update(entry: DiaryEntry): DiaryEntry
    fun findByUserEmail(userEmail: String): List<DiaryEntry>
    fun findByUserEmailAndDate(userEmail: String, diaryDate: LocalDate): List<DiaryEntry>
    fun findById(id: Long): DiaryEntry?
    fun delete(entry: DiaryEntry)
}