package com.example.personalmidterm.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.personalmidterm.model.Voucher
import com.example.personalmidterm.model.VoucherType

@Entity(tableName = "vouchers")
data class VoucherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val description: String,
    val type: VoucherType,
    val value: Long,
    val minSpend: Long,
    val maxDiscount: Long?,
    val expiryDate: Long
) {
    fun toDomainModel() = Voucher(
        id = id,
        code = code,
        description = description,
        type = type,
        value = value,
        minSpend = minSpend,
        maxDiscount = maxDiscount,
        expiryDate = expiryDate
    )

    companion object {
        fun fromDomainModel(voucher: Voucher) = VoucherEntity(
            id = voucher.id,
            code = voucher.code,
            description = voucher.description,
            type = voucher.type,
            value = voucher.value,
            minSpend = voucher.minSpend,
            maxDiscount = voucher.maxDiscount,
            expiryDate = voucher.expiryDate
        )
    }
}
