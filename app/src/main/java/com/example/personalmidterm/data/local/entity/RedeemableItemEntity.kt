package com.example.personalmidterm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personalmidterm.model.RedeemableItem

@Entity(tableName = "redeemable_items")
data class RedeemableItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val pointsCost: Int,
    val imageRes: Int
) {
    fun toDomainModel() = RedeemableItem(
        id = id,
        name = name,
        description = description,
        pointsCost = pointsCost,
        imageRes = imageRes
    )
}
