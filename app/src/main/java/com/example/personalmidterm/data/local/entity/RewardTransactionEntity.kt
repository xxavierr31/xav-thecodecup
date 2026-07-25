package com.example.personalmidterm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reward_transactions")
data class RewardTransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderId: Long?,
    val description: String,
    val points: Int,
    val timestamp: Long
)
