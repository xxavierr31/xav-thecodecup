package com.example.personalmidterm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personalmidterm.model.Category
import com.example.personalmidterm.model.Coffee

@Entity(tableName = "coffees")
data class CoffeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val basePrice: Long,
    val imageRes: Int,
    val category: Category,
    val canBeHot: Boolean = true,
    val canBeIced: Boolean = true
) {
    fun toDomainModel() = Coffee(
        id = id,
        name = name,
        description = description,
        basePrice = basePrice,
        imageRes = imageRes,
        category = category,
        canBeHot = canBeHot,
        canBeIced = canBeIced
    )
}
