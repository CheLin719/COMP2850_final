package com.comp2850.goodfood.recipes

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "recipes")
class RecipeEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String = "",

    @Column(nullable = true)
    var emoji: String? = null,

    @Column(nullable = true)
    var tag: String? = null,

    @Column(nullable = true)
    var kcal: Int? = null,

    @Column(nullable = true)
    var cost: String? = null,

    @Column(nullable = true)
    var timeMin: Int? = null,

    @Column(nullable = false, length = 4000)
    var ingredients: String = "[]",

    @Column(nullable = false, length = 8000)
    var steps: String = "[]"
)