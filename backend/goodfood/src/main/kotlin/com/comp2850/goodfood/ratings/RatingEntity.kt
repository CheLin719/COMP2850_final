package com.comp2850.goodfood.ratings

import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "ratings")
class RatingEntity(

    @EmbeddedId
    var id: RatingId = RatingId(),

    @Column(nullable = false)
    var score: Int = 1
)