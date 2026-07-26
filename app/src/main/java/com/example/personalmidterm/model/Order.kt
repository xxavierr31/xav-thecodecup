package com.example.personalmidterm.model

enum class OrderStatus {
    ONGOING, HISTORY
}

enum class OrderResultStatus {
    DELIVERED, CANCELLED
}

data class Order(
    val id: Long,
    val status: OrderStatus,
    val resultStatus: OrderResultStatus,
    val totalPrice: Long,
    val timestamp: Long,
    val address: String,
    val items: List<OrderItem> = emptyList()
)

data class OrderItem(
    val id: Long,
    val orderId: Long,
    val coffeeName: String,
    val coffeeImageRes: Int,
    val customization: Customization,
    val quantity: Int,
    val unitPrice: Long
)
