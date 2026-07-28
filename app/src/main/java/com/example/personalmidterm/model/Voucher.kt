package com.example.personalmidterm.model

enum class VoucherType {
    PERCENTAGE, FLAT
}

data class Voucher(
    val id: Long,
    val code: String,
    val description: String,
    val type: VoucherType,
    val value: Long,
    val minSpend: Long = 0L,
    val maxDiscount: Long? = null,
    val expiryDate: Long
)
