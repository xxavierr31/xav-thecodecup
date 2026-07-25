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
        setupRecyclerView()
        setupFilters()
        observeViewModel()
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
            onCoffeeClick = { coffee ->
                val bundle = bundleOf("coffeeId" to coffee.id)
                findNavController().navigate(R.id.action_menuFragment_to_detailsFragment, bundle)
            },
            onAddClick = { coffee ->
                val bundle = bundleOf("coffeeId" to coffee.id)
                findNavController().navigate(R.id.action_menuFragment_to_detailsFragment, bundle)
            }
        )
        binding.rvMenuProducts.layoutManager = GridLayoutManager(requireContext(), 2)
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
        if (category == Category.SPECIAL) {
            binding.btnTabSeasonal.setBackgroundColor(requireContext().getColor(R.color.theme_brown))
            binding.btnTabSeasonal.setTextColor(requireContext().getColor(R.color.white))
            
            binding.btnTabClassics.setBackgroundColor(requireContext().getColor(R.color.card_cream))
            binding.btnTabClassics.setTextColor(requireContext().getColor(R.color.text_secondary))
        } else {
            binding.btnTabSeasonal.setBackgroundColor(requireContext().getColor(R.color.card_cream))
            binding.btnTabSeasonal.setTextColor(requireContext().getColor(R.color.text_secondary))
            
            binding.btnTabClassics.setBackgroundColor(requireContext().getColor(R.color.theme_brown))
            binding.btnTabClassics.setTextColor(requireContext().getColor(R.color.white))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
