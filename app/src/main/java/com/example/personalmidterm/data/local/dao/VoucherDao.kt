package com.example.personalmidterm.data.local.dao

import androidx.room.*
import com.example.personalmidterm.data.local.entity.VoucherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VoucherDao {
    @Query("SELECT * FROM vouchers WHERE expiryDate > :currentTime")
    fun getAvailableVouchers(currentTime: Long): Flow<List<VoucherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vouchers: List<VoucherEntity>)

    @Query("SELECT * FROM vouchers WHERE id = :id")
    suspend fun getVoucherById(id: Long): VoucherEntity?
}
