package com.example.personalmidterm.ui.redeem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalmidterm.databinding.ItemMarketplaceBinding
import com.example.personalmidterm.model.RedeemableItem

class RedeemableAdapter(
    private val onRedeemClick: (RedeemableItem) -> Unit
) : RecyclerView.Adapter<RedeemableAdapter.RedeemableViewHolder>() {

    private var items: List<RedeemableItem> = emptyList()
    private var currentPoints: Int = 0

    fun submitList(newItems: List<RedeemableItem>, points: Int) {
        items = newItems
        currentPoints = points
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedeemableViewHolder {
        val binding = ItemMarketplaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RedeemableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RedeemableViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class RedeemableViewHolder(private val binding: ItemMarketplaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RedeemableItem) {
            binding.tvItemTitle.text = item.name
            binding.tvItemSubtitle.text = item.description
            binding.tvItemPoints.text = "${item.pointsCost} pts"
            binding.ivItemImage.setImageResource(item.imageRes)
            
            binding.btnRedeem.isEnabled = currentPoints >= item.pointsCost
            binding.btnRedeem.setOnClickListener { onRedeemClick(item) }
        }
    }
}
