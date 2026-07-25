package com.example.personalmidterm.data.repository

import com.example.personalmidterm.data.local.dao.CoffeeDao
import com.example.personalmidterm.model.Coffee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CoffeeRepository(private val coffeeDao: CoffeeDao) {
    val allCoffees: Flow<List<Coffee>> = coffeeDao.getAllCoffees().map { entities ->
        entities.map { it.toDomainModel() }
    }

    suspend fun getCoffeeById(id: Long): Coffee? {
        return coffeeDao.getCoffeeById(id)?.toDomainModel()
    }
}
