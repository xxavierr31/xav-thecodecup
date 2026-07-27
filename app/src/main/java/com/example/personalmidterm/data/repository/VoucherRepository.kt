package com.example.personalmidterm.data.repository

import com.example.personalmidterm.data.local.dao.VoucherDao
import com.example.personalmidterm.model.Voucher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

class VoucherRepository(private val voucherDao: VoucherDao) {
    
    private val _selectedVoucher = MutableStateFlow<Voucher?>(null)
    val selectedVoucher: StateFlow<Voucher?> = _selectedVoucher.asStateFlow()

    fun getAvailableVouchers(): Flow<List<Voucher>> {
        return voucherDao.getAvailableVouchers(System.currentTimeMillis()).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    fun selectVoucher(voucher: Voucher?) {
        _selectedVoucher.value = voucher
    }

    fun clearSelectedVoucher() {
        _selectedVoucher.value = null
    }
}
