package com.example.personalmidterm.data.local.dao

import androidx.room.*
import com.example.personalmidterm.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteEntity)

    @Query("SELECT EXISTS(SELECT * FROM favorites WHERE coffeeId = :coffeeId AND sweetness = :sweetness AND temperature = :temperature AND intensity = :temperatureLevel AND shots = :shots AND flavors = :flavors)")
    fun isFavorite(
        coffeeId: Long,
        sweetness: String,
        temperature: String,
        temperatureLevel: String,
        shots: Int,
        flavors: String
    ): Flow<Boolean>
}
