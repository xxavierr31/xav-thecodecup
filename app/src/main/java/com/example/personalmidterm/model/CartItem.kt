package com.example.personalmidterm.model

data class CartItem(
    val id: Long = 0,
    val coffee: Coffee,
    val customization: Customization,
    val quantity: Int,
    val unitPrice: Long
) {
    val totalPrice: Long get() = unitPrice * quantity
}
