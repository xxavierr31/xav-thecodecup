package com.example.personalmidterm.data.repository

import com.example.personalmidterm.data.local.dao.FavoriteDao
import com.example.personalmidterm.data.local.entity.FavoriteEntity
import com.example.personalmidterm.model.Coffee
import com.example.personalmidterm.model.Customization
import com.example.personalmidterm.model.Favorite
import kotlinx.coroutines.flow.Flow
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

    suspend fun toggleFavorite(coffee: Coffee, customization: Customization) {
        // Simple implementation: just insert for now. 
        // Real toggle would need to check if it already exists with these exact customizations.
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

    suspend fun removeFavorite(favorite: Favorite) {
        favoriteDao.deleteFavorite(
            FavoriteEntity(
                id = favorite.id,
                coffeeId = favorite.coffee.id,
                sweetness = favorite.customization.sweetness,
                temperature = favorite.customization.temperature,
                intensity = favorite.customization.intensity,
                shots = favorite.customization.shots,
                flavors = favorite.customization.flavors
            )
        )
    }
}
