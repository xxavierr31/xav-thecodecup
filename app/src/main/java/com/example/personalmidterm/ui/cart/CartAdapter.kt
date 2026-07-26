package com.example.personalmidterm.ui.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalmidterm.databinding.ItemCartBinding
import com.example.personalmidterm.model.CartItem
import com.example.personalmidterm.model.Temperature
import com.example.personalmidterm.util.CurrencyFormatter

class CartAdapter(
    private val onQuantityChange: (CartItem, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var items: List<CartItem> = emptyList()

    fun submitList(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    fun getItemAt(position: Int): CartItem = items[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItem) {
            binding.tvItemTitle.text = item.coffee.name
            
            val customizationText = buildString {
                append("${item.customization.temperature.label}, ")
                append("${item.customization.sweetness.label} Sweet, ")
                
                val levelLabel = if (item.customization.temperature == Temperature.ICED) "Ice" else "Heat"
                append("${item.customization.temperatureLevel.label} $levelLabel")
                
                if (item.customization.flavors.isNotEmpty()) {
                    append(", ")
                    append(item.customization.flavors.joinToString(", ") { it.label })
                }
            }
            binding.tvItemSubtitle.text = customizationText
            binding.tvItemPrice.text = CurrencyFormatter.format(item.unitPrice)
            binding.tvQtyCount.text = item.quantity.toString()
            binding.ivItemImage.setImageResource(item.coffee.imageRes)

            binding.btnQtyPlus.setOnClickListener { onQuantityChange(item, 1) }
            binding.btnQtyMinus.setOnClickListener { onQuantityChange(item, -1) }
        }
    }
}
