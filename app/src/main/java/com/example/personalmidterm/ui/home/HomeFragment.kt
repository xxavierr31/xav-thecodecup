package com.example.personalmidterm.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.personalmidterm.R
import com.example.personalmidterm.databinding.FragmentHomeBinding
import com.example.personalmidterm.model.Coffee
import com.example.personalmidterm.ui.ViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels { ViewModelFactory.Factory }
    private lateinit var adapter: SavedRecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = SavedRecipeAdapter { favorite ->
            val bundle = bundleOf(
                "coffeeId" to favorite.coffee.id,
                "customization" to favorite.customization
            )
            findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
        }
        binding.savedRecipesRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.savedRecipesRecycler.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.heroCoffee.collect { coffee ->
                        coffee?.let { bindHero(it) }
                    }
                }
                launch {
                    viewModel.favorites.collect { favorites ->
                        adapter.submitList(favorites)
                    }
                }
                launch {
                    viewModel.loyaltyState.collect { state ->
                        updateLoyaltyUi(state.stamps)
                    }
                }
            }
        }
    }

    private fun bindHero(coffee: Coffee) {
        binding.heroTitle.text = coffee.name
        binding.heroDescription.text = coffee.description
        binding.heroImage.setImageResource(coffee.imageRes)
        binding.heroButton.setOnClickListener {
            val bundle = bundleOf("coffeeId" to coffee.id)
            findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
        }
    }

    private fun updateLoyaltyUi(stamps: Int) {
        val stampsContainer = binding.loyaltyCard.stampsContainer
        binding.loyaltyCard.tvStampsCount.text = "$stamps/8 Blooming"
        
        for (i in 0 until stampsContainer.childCount) {
            val slot = stampsContainer.getChildAt(i)
            val icon = slot.findViewById<android.widget.ImageView>(R.id.stamp_icon)
            if (i < stamps) {
                icon?.setImageResource(R.drawable.ic_leaf)
                icon?.alpha = 1.0f
            } else {
                icon?.setImageResource(R.drawable.empty_stamp)
                icon?.alpha = 0.5f
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
