package com.example.personalmidterm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.personalmidterm.data.local.entity.RewardTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RewardDao {
    @Query("SELECT * FROM reward_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<RewardTransactionEntity>>

    @Insert
    suspend fun insertTransaction(transaction: RewardTransactionEntity)
}
