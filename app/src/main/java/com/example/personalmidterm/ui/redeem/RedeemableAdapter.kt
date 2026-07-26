package com.example.personalmidterm.ui.redeem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalmidterm.R
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
            
            val isRedeemable = currentPoints >= item.pointsCost
            binding.btnRedeem.isEnabled = isRedeemable
            
            val context = binding.root.context
            if (isRedeemable) {
                binding.btnRedeem.backgroundTintList = android.content.res.ColorStateList.valueOf(context.getColor(R.color.theme_brown))
                binding.btnRedeem.setTextColor(context.getColor(R.color.white))
                binding.tvItemPoints.setTextColor(context.getColor(R.color.theme_brown))
            } else {
                binding.btnRedeem.backgroundTintList = android.content.res.ColorStateList.valueOf(context.getColor(R.color.disallowed_redeem_bg))
                binding.btnRedeem.setTextColor(context.getColor(R.color.bg_grey))
                binding.tvItemPoints.setTextColor(context.getColor(R.color.disallowed_redeem_pts))
            }

            binding.btnRedeem.setOnClickListener { onRedeemClick(item) }
        }
    }
}
