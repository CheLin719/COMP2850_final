package com.comp2850.goodfood.nutrition

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "food_nutrition")
class FoodNutritionEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var foodName: String = "",

    @Column(nullable = false)
    var calories: Double = 0.0,

    @Column(nullable = false)
    var protein: Double = 0.0,

    @Column(nullable = false)
    var sugar: Double = 0.0
)