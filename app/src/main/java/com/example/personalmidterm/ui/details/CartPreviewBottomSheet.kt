package com.example.personalmidterm.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.personalmidterm.R
import com.example.personalmidterm.databinding.BottomsheetCartBinding
import com.example.personalmidterm.ui.ViewModelFactory
import com.example.personalmidterm.ui.cart.CartAdapter
import com.example.personalmidterm.ui.cart.CartViewModel
import com.example.personalmidterm.util.CurrencyFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch

class CartPreviewBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels { ViewModelFactory.Factory }
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter { item, delta ->
            viewModel.updateQuantity(item, delta)
        }
        binding.rvCartPreview.adapter = adapter
    }

    private fun setupListeners() {
        binding.sheetCloseBtn.setOnClickListener {
            dismiss()
        }
        binding.btnGoToCart.setOnClickListener {
            dismiss()
            findNavController().navigate(R.id.cartFragment)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cartItems.collect { items ->
                        adapter.submitList(items)
                        val isEmpty = items.isEmpty()
                        binding.cartContentGroup.visibility = if (isEmpty) View.GONE else View.VISIBLE
                        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.total.collect { total ->
                        binding.tvSheetTotalVal.text = CurrencyFormatter.format(total)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
