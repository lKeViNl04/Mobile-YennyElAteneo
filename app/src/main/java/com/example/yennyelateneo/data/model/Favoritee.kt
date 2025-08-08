package com.example.yennyelateneo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Favoritee(
    val id: Long,
    val user_id: String,
    val book_id: Long
)

fun Favoritee.toJavaFavorite(): Favorite {
    return Favorite(
        this.id,
        this.user_id,
        this.book_id
    )
}