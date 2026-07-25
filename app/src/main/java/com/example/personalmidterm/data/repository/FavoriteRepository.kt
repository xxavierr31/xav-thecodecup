package com.example.personalmidterm.data.repository

import com.example.personalmidterm.data.local.dao.FavoriteDao
import com.example.personalmidterm.data.local.entity.FavoriteEntity
import com.example.personalmidterm.model.Coffee
import com.example.personalmidterm.model.Customization
import com.example.personalmidterm.model.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FavoriteRepository(
    private val favoriteDao: FavoriteDao,
    private val coffeeRepository: CoffeeRepository
) {
    val allFavorites: Flow<List<Favorite>> = favoriteDao.getAllFavorites().map { entities ->
        entities.map { entity ->
            val coffee = coffeeRepository.getCoffeeById(entity.coffeeId)!!
            entity.toDomainModel(coffee)
        }
    }

    suspend fun addFavorite(coffee: Coffee, customization: Customization) {
        favoriteDao.insertFavorite(
            FavoriteEntity(
                coffeeId = coffee.id,
                sweetness = customization.sweetness,
                temperature = customization.temperature,
                intensity = customization.intensity,
                shots = customization.shots,
                flavors = customization.flavors
            )
        )
    }

    suspend fun removeFavoriteByCoffeeId(coffeeId: Long) {
        val favorites = favoriteDao.getAllFavorites().first()
        val toDelete = favorites.filter { it.coffeeId == coffeeId }
        toDelete.forEach { favoriteDao.deleteFavorite(it) }
    }

    suspend fun removeFavorite(favoriteId: Long) {
        val favorites = favoriteDao.getAllFavorites().first()
        val toDelete = favorites.find { it.id == favoriteId }
        toDelete?.let { favoriteDao.deleteFavorite(it) }
    }
}
