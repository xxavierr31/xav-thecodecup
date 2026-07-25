package com.example.personalmidterm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personalmidterm.model.*

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coffeeId: Long,
    val sweetness: Sweetness,
    val temperature: Temperature,
    val intensity: Intensity,
    val shots: Int,
    val flavors: List<Flavor>
) {
    fun toDomainModel(coffee: Coffee) = Favorite(
        id = id,
        coffee = coffee,
        customization = Customization(
            sweetness = sweetness,
            temperature = temperature,
            intensity = intensity,
            shots = shots,
            flavors = flavors
        )
    )
}
