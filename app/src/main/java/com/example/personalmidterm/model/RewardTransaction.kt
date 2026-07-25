package com.example.personalmidterm.model

data class RewardTransaction(
    val id: Long,
    val orderId: Long?,
    val description: String,
    val points: Int,
    val timestamp: Long
)
