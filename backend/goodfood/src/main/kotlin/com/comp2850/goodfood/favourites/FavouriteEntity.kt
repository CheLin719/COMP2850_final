package com.comp2850.goodfood.favourites

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "favourites")
class FavouriteEntity(

    @EmbeddedId
    var id: FavouriteId = FavouriteId()
)