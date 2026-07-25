package com.example.personalmidterm.ui.rewards

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalmidterm.databinding.ItemRecentActivityBinding
import com.example.personalmidterm.model.RewardTransaction
import java.text.SimpleDateFormat
import java.util.*

class ActivityAdapter : RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder>() {

    private var transactions: List<RewardTransaction> = emptyList()
    private val dateFormat = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())

    fun submitList(newTransactions: List<RewardTransaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ItemRecentActivityBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    inner class ActivityViewHolder(private val binding: ItemRecentActivityBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: RewardTransaction) {
            binding.tvActivityTitle.text = transaction.description
            binding.tvActivitySubtitle.text = dateFormat.format(Date(transaction.timestamp))
            binding.tvActivityPoints.text = "+${transaction.points} pts"
        }
    }
}
