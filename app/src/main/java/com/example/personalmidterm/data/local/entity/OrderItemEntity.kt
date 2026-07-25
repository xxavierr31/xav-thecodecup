package com.example.personalmidterm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personalmidterm.model.*

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long,
    val coffeeName: String,
    val coffeeImageRes: Int,
    val sweetness: Sweetness,
    val temperature: Temperature,
    val intensity: Intensity,
    val shots: Int,
    val flavors: List<Flavor>,
    val quantity: Int,
    val unitPrice: Long
) {
    fun toDomainModel() = OrderItem(
        id = id,
        orderId = orderId,
        coffeeName = coffeeName,
        coffeeImageRes = coffeeImageRes,
        customization = Customization(
            sweetness = sweetness,
            temperature = temperature,
            intensity = intensity,
            shots = shots,
            flavors = flavors
        ),
        quantity = quantity,
        unitPrice = unitPrice
    )
}
