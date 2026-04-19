package com.comp2850.goodfood.nutrition

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "nutrition_guides")
class NutritionGuideEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var nutrient: String = "",

    @Column(nullable = false)
    var dailyTarget: Double = 0.0,

    @Column(nullable = false)
    var unit: String = ""
)