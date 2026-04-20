package com.comp2850.goodfood.appointments

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "appointments")
class AppointmentEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "pro_id", nullable = false)
    var proId: String = "",

    @Column(name = "client_id", nullable = false)
    var clientId: String = "",

    @Column(nullable = false)
    var date: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    var time: LocalTime = LocalTime.of(9, 0),

    @Column(nullable = true)
    var type: String? = null,

    @Column(nullable = false)
    var status: String = "pending",

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)