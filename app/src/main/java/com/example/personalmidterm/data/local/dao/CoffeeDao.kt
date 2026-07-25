package com.example.personalmidterm.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.personalmidterm.data.local.entity.CoffeeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeDao {
    @Query("SELECT * FROM coffees")
    fun getAllCoffees(): Flow<List<CoffeeEntity>>

    @Query("SELECT * FROM coffees WHERE id = :id")
    suspend fun getCoffeeById(id: Long): CoffeeEntity?

    @Insert
    suspend fun insertAll(coffees: List<CoffeeEntity>)
}
