package com.example.personalmidterm.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personalmidterm.model.*

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coffeeId: Long,
    val coffeeName: String,
    val coffeeImageRes: Int,
    val sweetness: Sweetness,
    val temperature: Temperature,
    @ColumnInfo(name = "intensity") val temperatureLevel: TemperatureLevel,
    val shots: Int,
    val flavors: List<Flavor>,
    val quantity: Int,
    val unitPrice: Long
) {
    fun toDomainModel(coffee: Coffee) = CartItem(
        id = id,
        coffee = coffee,
        customization = Customization(
            sweetness = sweetness,
            temperature = temperature,
            temperatureLevel = temperatureLevel,
            shots = shots,
            flavors = flavors
        ),
        quantity = quantity,
        unitPrice = unitPrice
    )
}
