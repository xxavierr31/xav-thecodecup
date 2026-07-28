package com.example.personalmidterm.ui.menu

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
import com.example.personalmidterm.databinding.FragmentMenuBinding
import com.example.personalmidterm.model.Category
import com.example.personalmidterm.ui.ViewModelFactory
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.launch

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MenuViewModel by viewModels { ViewModelFactory.Factory }
    private lateinit var adapter: CoffeeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupHeader()
        setupSearch()
        setupRecyclerView()
        setupFilters()
        observeViewModel()
    }

    private fun setupSearch() {
        binding.etMenuSearch.doOnTextChanged { text, _, _, _ ->
            viewModel.updateSearchQuery(text?.toString() ?: "")
        }
    }

    private fun setupHeader() {
        binding.header.screenTitle.text = "Menu"
        binding.header.screenTitle.visibility = View.VISIBLE
        binding.header.backButton.visibility = View.GONE
        binding.header.favoriteIcon.visibility = View.GONE
        binding.header.cartIcon.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        adapter = CoffeeAdapter(
            onAddClick = { coffee ->
                val bundle = bundleOf("coffeeId" to coffee.id)
                findNavController().navigate(R.id.action_menuFragment_to_detailsFragment, bundle)
            }
        )
        val spanCount = resources.getInteger(R.integer.menu_grid_span)
        binding.rvMenuProducts.layoutManager = GridLayoutManager(requireContext(), spanCount)
        binding.rvMenuProducts.adapter = adapter
    }

    private fun setupFilters() {
        binding.btnTabSeasonal.setOnClickListener {
            viewModel.setCategory(Category.SPECIAL)
        }
        binding.btnTabClassics.setOnClickListener {
            viewModel.setCategory(Category.CLASSIC)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.filteredCoffees.collect { coffees ->
                        adapter.submitList(coffees)
                    }
                }
                launch {
                    viewModel.selectedCategory.collect { category ->
                        updateFilterUi(category)
                    }
                }
            }
        }
    }

    private fun updateFilterUi(category: Category) {
        val brown = android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.theme_brown))
        val cream = android.content.res.ColorStateList.valueOf(requireContext().getColor(R.color.card_cream))
        val white = requireContext().getColor(R.color.white)
        val grey = requireContext().getColor(R.color.text_secondary)

        if (category == Category.SPECIAL) {
            binding.btnTabSeasonal.backgroundTintList = brown
            binding.btnTabSeasonal.setTextColor(white)
            
            binding.btnTabClassics.backgroundTintList = cream
            binding.btnTabClassics.setTextColor(grey)
        } else {
            binding.btnTabSeasonal.backgroundTintList = cream
            binding.btnTabSeasonal.setTextColor(grey)
            
            binding.btnTabClassics.backgroundTintList = brown
            binding.btnTabClassics.setTextColor(white)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
