package com.example.personalmidterm.ui.myorders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalmidterm.R
import com.example.personalmidterm.databinding.ItemOrderBinding
import com.example.personalmidterm.model.Order
import com.example.personalmidterm.model.OrderStatus
import com.example.personalmidterm.util.CurrencyFormatter
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var orders: List<Order> = emptyList()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    fun submitList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(private val binding: ItemOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: Order) {
            binding.tvOrderDate.text = dateFormat.format(Date(order.timestamp))
            binding.tvOrderPrice.text = CurrencyFormatter.format(order.totalPrice)
            
            val itemsSummary = order.items.joinToString(", ") { it.coffeeName }
            binding.tvOrderItems.text = itemsSummary
            
            val isOngoing = order.status == OrderStatus.ONGOING
            binding.tvStatusBadge.text = if (isOngoing) "Preparing" else "Delivered"
            binding.statusBadgeBg.setCardBackgroundColor(
                binding.root.context.getColor(
                    if (isOngoing) R.color.theme_maroon else R.color.theme_green_badge
                )
            )
            binding.tvStatusBadge.setTextColor(binding.root.context.getColor(R.color.white))

            binding.root.setOnClickListener { onOrderClick(order) }
        }
    }
}
