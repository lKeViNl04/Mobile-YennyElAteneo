package com.example.yennyelateneo.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Bookk(
    val id: Long? = null,
    val title: String,
    val author: String,
    val description: String,
    val price: Double,
    val image: String? = null
)

fun Bookk.toJavaBook(): Book {
    return Book(
        this.id!!,
        this.title,
        this.author,
        this.description,
        this.price,
        this.image
    )
}
