package com.example.personalmidterm.ui.redeem

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
import com.example.personalmidterm.databinding.FragmentRedeemBinding
import com.example.personalmidterm.ui.ViewModelFactory
import kotlinx.coroutines.launch

class RedeemFragment : Fragment() {

    private var _binding: FragmentRedeemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RedeemViewModel by viewModels { ViewModelFactory.Factory }
    private lateinit var adapter: RedeemableAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRedeemBinding.inflate(inflater, container, false)
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
        binding.header.screenTitle.text = "Redeem"
        binding.header.screenTitle.visibility = View.VISIBLE
        binding.header.backButton.visibility = View.VISIBLE
        binding.header.favoriteIcon.visibility = View.GONE
        binding.header.cartIcon.visibility = View.GONE
    }

    private fun setupRecyclerView() {
        adapter = RedeemableAdapter { item ->
            viewModel.redeem(item)
        }
        binding.rvMarketplace.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loyaltyState.collect { state ->
                        binding.tvBalanceVal.text = state.totalPoints.toString()
                        viewModel.redeemables.value.let { items ->
                            adapter.submitList(items, state.totalPoints)
                        }
                    }
                }
                launch {
                    viewModel.redeemables.collect { items ->
                        adapter.submitList(items, viewModel.loyaltyState.value.totalPoints)
                    }
                }
                launch {
                    viewModel.canRedeemAnything.collect { canRedeem ->
                        binding.tvBalanceSubtitle.text = if (canRedeem) {
                            "You have enough points to redeem a gift!"
                        } else {
                            "Start placing orders to earn more points."
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
