package com.example.personalmidterm.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalmidterm.databinding.ItemRecipeBinding
import com.example.personalmidterm.model.Favorite
import com.example.personalmidterm.model.Temperature
import com.example.personalmidterm.util.CurrencyFormatter

class SavedRecipeAdapter(
    private val onRecipeClick: (Favorite) -> Unit
) : RecyclerView.Adapter<SavedRecipeAdapter.RecipeViewHolder>() {

    private var favorites: List<Favorite> = emptyList()

    fun submitList(newFavorites: List<Favorite>) {
        favorites = newFavorites
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(favorites[position])
    }

    override fun getItemCount(): Int = favorites.size

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: Favorite) {
            binding.recipeName.text = favorite.coffee.name
            binding.heroImage.setImageResource(favorite.coffee.imageRes)
            
            // Customization summary
            val customizationText = buildString {
                append("${favorite.customization.temperature.label}, ")
                append("${favorite.customization.sweetness.label} Sweet, ")
                
                val levelLabel = if (favorite.customization.temperature == Temperature.ICED) "Ice" else "Heat"
                append("${favorite.customization.temperatureLevel.label} $levelLabel")
                
                if (favorite.customization.shots > 0) {
                    append(", ${favorite.customization.shots} extra shots")
                }
                
                if (favorite.customization.flavors.isNotEmpty()) {
                    append(", ")
                    append(favorite.customization.flavors.joinToString(", ") { it.label })
                }
            }
            binding.recipeDetails.text = customizationText
            
            // Price calculation
            val flavorSurcharge = favorite.customization.flavors.size * 5000L
            val shotSurcharge = favorite.customization.shots * 7000L
            val totalPrice = favorite.coffee.basePrice + flavorSurcharge + shotSurcharge
            binding.recipePrice.text = CurrencyFormatter.format(totalPrice)
            
            binding.root.setOnClickListener { onRecipeClick(favorite) }
        }
    }
}
