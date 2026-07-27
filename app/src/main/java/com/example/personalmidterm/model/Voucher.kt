package com.example.personalmidterm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class VoucherType {
    PERCENTAGE, FLAT
}

@Parcelize
data class Voucher(
    val id: Long,
    val code: String,
    val description: String,
    val type: VoucherType,
    val value: Long, // Percentage (e.g., 10) or Flat Amount (e.g., 20000)
    val minSpend: Long = 0L,
    val maxDiscount: Long? = null,
    val expiryDate: Long // Timestamp
) : Parcelable
