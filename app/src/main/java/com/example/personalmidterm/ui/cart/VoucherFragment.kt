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
import com.example.personalmidterm.databinding.FragmentVouchersBinding
import com.example.personalmidterm.ui.ViewModelFactory
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class VoucherFragment : Fragment() {

    private var _binding: FragmentVouchersBinding? = null
    private val binding get() = _binding!!

    private val voucherViewModel: VoucherViewModel by viewModels { ViewModelFactory.Factory }
    private val cartViewModel: CartViewModel by viewModels { ViewModelFactory.Factory }
    private lateinit var adapter: VoucherAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVouchersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupHeader()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupHeader() {
        binding.header.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.header.backButton.visibility = View.VISIBLE
        binding.header.screenTitle.text = "Select Voucher"
        binding.header.screenTitle.visibility = View.VISIBLE
        binding.header.favoriteIcon.visibility = View.GONE
        binding.header.cartIcon.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        adapter = VoucherAdapter { voucher ->
            voucherViewModel.selectVoucher(voucher)
        }
        binding.rvVouchers.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(
                    voucherViewModel.vouchers,
                    cartViewModel.subtotal,
                    voucherViewModel.selectedVoucher
                ) { vouchers, subtotal, selected ->
                    Triple(vouchers, subtotal, selected)
                }.collect { (vouchers, subtotal, selected) ->
                    binding.tvNoVouchers.visibility = if (vouchers.isEmpty()) View.VISIBLE else View.GONE
                    adapter.submitList(vouchers, subtotal, selected?.id)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
