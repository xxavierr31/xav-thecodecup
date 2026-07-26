package com.example.personalmidterm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personalmidterm.model.Order
import com.example.personalmidterm.model.OrderItem
import com.example.personalmidterm.model.OrderResultStatus
import com.example.personalmidterm.model.OrderStatus

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val status: OrderStatus,
    val resultStatus: OrderResultStatus,
    val totalPrice: Long,
    val timestamp: Long,
    val address: String
) {
    fun toDomainModel(items: List<OrderItem>) = Order(
        id = id,
        status = status,
        resultStatus = resultStatus,
        totalPrice = totalPrice,
        timestamp = timestamp,
        address = address,
        items = items
    )
}
