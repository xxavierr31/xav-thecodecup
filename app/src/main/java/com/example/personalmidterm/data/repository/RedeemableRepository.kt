package com.example.personalmidterm.data.repository

import com.example.personalmidterm.data.local.dao.RedeemableDao
import com.example.personalmidterm.model.RedeemableItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RedeemableRepository(private val redeemableDao: RedeemableDao) {
    val allRedeemables: Flow<List<RedeemableItem>> = redeemableDao.getAllRedeemables().map { entities ->
        entities.map { it.toDomainModel() }
    }
}
