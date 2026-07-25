package com.example.personalmidterm.data.repository

import com.example.personalmidterm.data.local.dao.RewardDao
import com.example.personalmidterm.data.local.entity.RewardTransactionEntity
import com.example.personalmidterm.data.prefs.LoyaltyPrefs
import com.example.personalmidterm.model.RewardTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class RewardRepository(
    private val rewardDao: RewardDao,
    private val loyaltyPrefs: LoyaltyPrefs
) {
    val allTransactions: Flow<List<RewardTransaction>> = rewardDao.getAllTransactions().map { entities ->
        entities.map { entity ->
            RewardTransaction(
                id = entity.id,
                orderId = entity.orderId,
                description = entity.description,
                points = entity.points,
                timestamp = entity.timestamp
            )
        }
    }

    suspend fun redeemPoints(points: Int, description: String) {
        val loyaltyState = loyaltyPrefs.loyaltyState.first()
        if (loyaltyState.totalPoints >= points) {
            loyaltyPrefs.updateLoyalty(
                loyaltyState.stamps,
                loyaltyState.totalPoints - points,
                loyaltyState.rankIndex
            )
            
            rewardDao.insertTransaction(
                RewardTransactionEntity(
                    orderId = null,
                    description = description,
                    points = -points,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}
