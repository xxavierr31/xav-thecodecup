package com.example.personalmidterm.ui.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalmidterm.R
import com.example.personalmidterm.databinding.ItemVoucherBinding
import com.example.personalmidterm.model.Voucher
import com.example.personalmidterm.model.VoucherType
import com.example.personalmidterm.util.CurrencyFormatter

class VoucherAdapter(
    private val onVoucherClick: (Voucher) -> Unit
) : RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder>() {

    private var vouchers: List<Voucher> = emptyList()
    private var subtotal: Long = 0L
    private var selectedVoucherId: Long? = null

    fun submitList(newVouchers: List<Voucher>, currentSubtotal: Long, selectedId: Long?) {
        vouchers = newVouchers
        subtotal = currentSubtotal
        selectedVoucherId = selectedId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoucherViewHolder {
        val binding = ItemVoucherBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VoucherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VoucherViewHolder, position: Int) {
        holder.bind(vouchers[position])
    }

    override fun getItemCount(): Int = vouchers.size

    inner class VoucherViewHolder(private val binding: ItemVoucherBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(voucher: Voucher) {
            binding.tvVoucherCode.text = voucher.code
            binding.tvVoucherDesc.text = voucher.description
            
            val isEligible = subtotal > 0 && subtotal >= voucher.minSpend
            val isSelected = voucher.id == selectedVoucherId

            if (isEligible) {
                binding.root.alpha = 1.0f
                binding.tvVoucherStatus.visibility = View.GONE
                binding.root.isEnabled = true
                binding.root.setOnClickListener { onVoucherClick(voucher) }
            } else {
                binding.root.alpha = 0.5f
                binding.tvVoucherStatus.text = "Min Spend: ${CurrencyFormatter.format(voucher.minSpend)}"
                binding.tvVoucherStatus.visibility = View.VISIBLE
                binding.root.isEnabled = false
            }

            if (isSelected) {
                binding.voucherCard.strokeColor = binding.root.context.getColor(R.color.theme_brown)
                binding.voucherCard.strokeWidth = 5
                binding.rbVoucherSelect.isChecked = true
            } else {
                binding.voucherCard.strokeColor = binding.root.context.getColor(R.color.divider_light)
                binding.voucherCard.strokeWidth = 2
                binding.rbVoucherSelect.isChecked = false
            }
        }
    }
}
