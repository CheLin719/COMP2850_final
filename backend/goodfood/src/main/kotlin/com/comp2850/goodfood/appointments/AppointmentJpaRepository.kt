package com.comp2850.goodfood.appointments

import org.springframework.data.jpa.repository.JpaRepository

interface AppointmentJpaRepository : JpaRepository<AppointmentEntity, Long> {
    fun findAllByProIdOrderByDateAscTimeAsc(proId: String): List<AppointmentEntity>
    fun findAllByClientIdOrderByDateAscTimeAsc(clientId: String): List<AppointmentEntity>
}