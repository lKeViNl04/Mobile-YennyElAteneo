package com.example.yennyelateneo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Revieww(
    val id: Long,
    val user_id: String,
    val book_id: Long,
    val liked: Boolean,
    val disliked: Boolean
)

fun Revieww.toJavaReview(): Review {
    return Review(
        //terminar de hacer aca
    )
}
