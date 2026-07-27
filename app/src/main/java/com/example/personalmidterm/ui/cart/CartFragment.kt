package com.example.personalmidterm.ui.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.personalmidterm.R
import com.example.personalmidterm.databinding.FragmentCartBinding
import com.example.personalmidterm.ui.ViewModelFactory
import com.example.personalmidterm.util.CurrencyFormatter
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels { ViewModelFactory.Factory }
    private lateinit var adapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupHeader() {
        binding.header.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.header.screenTitle.text = "My Cart"
        binding.header.screenTitle.visibility = View.VISIBLE
        binding.header.backButton.visibility = View.VISIBLE
        binding.header.favoriteIcon.visibility = View.GONE
        
        // Swap Cart icon for Voucher icon
        binding.header.cartIcon.setImageResource(R.drawable.ic_voucher)
        binding.header.cartIcon.visibility = View.VISIBLE
        binding.header.cartIcon.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_voucherFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter { item, delta ->
            viewModel.updateQuantity(item, delta)
        }
        binding.rvCartItems.adapter = adapter

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.getItemAt(vh.adapterPosition)
                viewModel.removeItem(item)
                
                com.google.android.material.snackbar.Snackbar.make(
                    binding.root,
                    "Item removed from cart",
                    com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                ).setAction("UNDO") {
                    viewModel.undoDelete()
                }.show()
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(binding.rvCartItems)
    }

    private fun setupListeners() {
        binding.btnCheckout.setOnClickListener {
            viewModel.checkout { orderId ->
                findNavController().navigate(R.id.action_cartFragment_to_orderSuccessFragment)
            }
        }
        binding.btnShopping.setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
        binding.emptyState.findViewById<View>(R.id.btn_browse_menu).setOnClickListener {
            findNavController().navigate(R.id.menuFragment)
        }
        binding.btnToggleBreakdown.setOnClickListener {
            val isVisible = binding.breakdownContainer.visibility == View.VISIBLE
            val nextVisibility = if (isVisible) View.GONE else View.VISIBLE
            binding.breakdownContainer.visibility = nextVisibility
            binding.divider.visibility = nextVisibility
            binding.btnToggleBreakdown.animate().rotation(if (isVisible) -180f else 0f).setDuration(200).start()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cartItems.collect { items ->
                        adapter.submitList(items)
                        binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                        binding.rvCartItems.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
                        binding.btnCheckout.isEnabled = items.isNotEmpty()
                        
                        // Hide voucher icon if cart is empty
                        binding.header.cartIcon.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
                    }
                }
                launch {
                    viewModel.total.collect { total ->
                        binding.tvEstimatedTotalVal.text = CurrencyFormatter.format(total)
                    }
                }
                launch {
                    viewModel.discountUiState.collect { state ->
                        binding.tvSubtotalVal.text = CurrencyFormatter.format(state.subtotal)
                        
                        if (state.isRankDiscountVisible) {
                            binding.tvRankPerk.visibility = View.VISIBLE
                            binding.tvRankPerkVal.visibility = View.VISIBLE
                            binding.tvRankPerk.text = "${state.rankName} (${state.discountPercent}% off)"
                            binding.tvRankPerkVal.text = "-${CurrencyFormatter.format(state.rankDiscountAmount)}"
                        } else {
                            binding.tvRankPerk.visibility = View.GONE
                            binding.tvRankPerkVal.visibility = View.GONE
                        }

                        if (state.isVoucherDiscountVisible) {
                            binding.tvVoucherLabel.visibility = View.VISIBLE
                            binding.tvVoucherVal.visibility = View.VISIBLE
                            binding.tvVoucherLabel.text = "Voucher (${state.voucherCode})"
                            binding.tvVoucherVal.text = "-${CurrencyFormatter.format(state.voucherDiscountAmount)}"
                        } else {
                            binding.tvVoucherLabel.visibility = View.GONE
                            binding.tvVoucherVal.visibility = View.GONE
                        }

                        if (state.isRankDiscountVisible || state.isVoucherDiscountVisible) {
                            binding.btnToggleBreakdown.visibility = View.VISIBLE
                            binding.tvOldTotalVal.visibility = View.VISIBLE
                            binding.tvOldTotalVal.text = CurrencyFormatter.format(state.subtotal)
                            binding.tvOldTotalVal.paintFlags = binding.tvOldTotalVal.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                            
                            // Sync divider with container visibility
                            binding.divider.visibility = binding.breakdownContainer.visibility
                        } else {
                            binding.btnToggleBreakdown.visibility = View.GONE
                            binding.breakdownContainer.visibility = View.GONE
                            binding.divider.visibility = View.GONE
                            binding.tvOldTotalVal.visibility = View.GONE
                        }
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
