package com.example.personalmidterm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.personalmidterm.data.local.entity.RedeemableItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RedeemableDao {
    @Query("SELECT * FROM redeemable_items")
    fun getAllRedeemables(): Flow<List<RedeemableItemEntity>>

    @Insert
    suspend fun insertAll(items: List<RedeemableItemEntity>)
}
