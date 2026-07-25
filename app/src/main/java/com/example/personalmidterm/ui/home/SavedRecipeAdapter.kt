package com.example.personalmidterm.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.personalmidterm.databinding.LayoutRecipeBinding
import com.example.personalmidterm.model.Favorite

class SavedRecipeAdapter(
    private val onRecipeClick: (Favorite) -> Unit
) : RecyclerView.Adapter<SavedRecipeAdapter.RecipeViewHolder>() {

    private var favorites: List<Favorite> = emptyList()

    fun submitList(newFavorites: List<Favorite>) {
        favorites = newFavorites
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = LayoutRecipeBinding.inflate(
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

    inner class RecipeViewHolder(private val binding: LayoutRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favorite: Favorite) {
            binding.recipeName.text = favorite.coffee.name
            binding.heroImage.setImageResource(favorite.coffee.imageRes)
            
            binding.root.setOnClickListener { onRecipeClick(favorite) }
        }
    }
}
