package com.example.personalmidterm.model

enum class Category {
    SPECIAL, CLASSIC
}

data class Coffee(
    val id: Long,
    val name: String,
    val description: String,
    val basePrice: Long,
    val imageRes: Int,
    val category: Category,
    val canBeHot: Boolean = true,
    val canBeIced: Boolean = true
)
