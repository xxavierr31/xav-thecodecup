package com.example.personalmidterm.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.repository.VoucherRepository
import com.example.personalmidterm.model.Voucher
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VoucherViewModel(
    private val voucherRepository: VoucherRepository
) : ViewModel() {

    val vouchers: StateFlow<List<Voucher>> = voucherRepository.getAvailableVouchers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedVoucher: StateFlow<Voucher?> = voucherRepository.selectedVoucher

    fun selectVoucher(voucher: Voucher?) {
        val current = selectedVoucher.value
        if (voucher != null && current?.id == voucher.id) {
            voucherRepository.clearSelectedVoucher()
        } else {
            voucherRepository.selectVoucher(voucher)
        }
    }
}
