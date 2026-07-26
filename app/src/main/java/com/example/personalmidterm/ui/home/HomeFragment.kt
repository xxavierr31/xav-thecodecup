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
import android.net.Uri
import java.io.File
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
        
        setupHeader()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupHeader() {
        binding.header.userAvatar.visibility = View.VISIBLE
        binding.header.greetingContainer.visibility = View.VISIBLE
        binding.header.cartIcon.visibility = View.VISIBLE
        binding.header.favoriteIcon.visibility = View.GONE
        
        binding.header.userAvatar.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
        binding.header.cartIcon.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = SavedRecipeAdapter { favorite ->
            val bundle = bundleOf(
                "coffeeId" to favorite.coffee.id,
                "customization" to favorite.customization
            )
            findNavController().navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
        }
        val spanCount = resources.getInteger(R.integer.recipe_grid_span)
        binding.savedRecipesRecycler.layoutManager = GridLayoutManager(requireContext(), spanCount)
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
                launch {
                    viewModel.profile.collect { profile ->
                        val greeting = getGreeting()
                        binding.header.greetingText.text = "$greeting, ${profile.name}"
                        
                        if (profile.imagePath != null) {
                            val file = File(profile.imagePath)
                            if (file.exists()) {
                                binding.header.userAvatar.setPadding(0, 0, 0, 0)
                                binding.header.userAvatar.imageTintList = null
                                binding.header.userAvatar.setImageURI(Uri.fromFile(file))
                            } else {
                                binding.header.userAvatar.setPadding(8, 8, 8, 8)
                                binding.header.userAvatar.imageTintList = android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.bg_grey))
                                binding.header.userAvatar.setImageResource(R.drawable.profile)
                            }
                        } else {
                            binding.header.userAvatar.setPadding(8, 8, 8, 8)
                            binding.header.userAvatar.imageTintList = android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.bg_grey))
                            binding.header.userAvatar.setImageResource(R.drawable.profile)
                        }
                    }
                }
            }
        }
    }

    private fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good Morning"
            in 12..17 -> "Good Afternoon"
            else -> "Good Evening"
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
        binding.loyaltyCard.tvStampsCount.text = "$stamps/8 Stamps"
        
        val density = resources.displayMetrics.density
        val leafPadding = (12 * density).toInt()
        val emptyPadding = 0

        for (i in 0 until stampsContainer.childCount) {
            val slot = stampsContainer.getChildAt(i)
            val icon = slot.findViewById<android.widget.ImageView>(R.id.stamp_icon)
            if (i < stamps) {
                icon?.setPadding(leafPadding, leafPadding, leafPadding, leafPadding)
                icon?.setImageResource(R.drawable.ic_leaf)
                icon?.alpha = 1.0f
            } else {
                icon?.setPadding(emptyPadding, emptyPadding, emptyPadding, emptyPadding)
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
