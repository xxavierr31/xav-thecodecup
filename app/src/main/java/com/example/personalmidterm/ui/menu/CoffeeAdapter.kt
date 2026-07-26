package com.example.personalmidterm.ui.menu

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalmidterm.databinding.ItemMenuProductBinding
import com.example.personalmidterm.model.Coffee
import com.example.personalmidterm.util.CurrencyFormatter

class CoffeeAdapter(
    private val onAddClick: (Coffee) -> Unit
) : RecyclerView.Adapter<CoffeeAdapter.CoffeeViewHolder>() {

    private var coffees: List<Coffee> = emptyList()

    fun submitList(newCoffees: List<Coffee>) {
        coffees = newCoffees
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoffeeViewHolder {
        val binding = ItemMenuProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CoffeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoffeeViewHolder, position: Int) {
        holder.bind(coffees[position])
    }

    override fun getItemCount(): Int = coffees.size

    inner class CoffeeViewHolder(private val binding: ItemMenuProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(coffee: Coffee) {
            binding.tvProductTitle.text = coffee.name
            binding.tvProductSubtitle.text = coffee.description
            binding.tvProductPrice.text = CurrencyFormatter.format(coffee.basePrice)
            binding.ivProductImage.setImageResource(coffee.imageRes)

            binding.btnAddProduct.setOnClickListener { onAddClick(coffee) }
        }
    }
}
