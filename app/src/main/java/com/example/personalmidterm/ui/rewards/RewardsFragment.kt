package com.example.personalmidterm.ui.rewards

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
import com.example.personalmidterm.R
import com.example.personalmidterm.databinding.FragmentRewardsBinding
import com.example.personalmidterm.ui.ViewModelFactory
import kotlinx.coroutines.launch

class RewardsFragment : Fragment() {

    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RewardsViewModel by viewModels { ViewModelFactory.Factory }
    private lateinit var adapter: ActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupHeader()
        setupListeners()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupHeader() {
        binding.header.screenTitle.text = "Rewards"
        binding.header.screenTitle.visibility = View.VISIBLE
        binding.header.backButton.visibility = View.GONE
        binding.header.favoriteIcon.visibility = View.GONE
        binding.header.cartIcon.visibility = View.GONE
    }

    private fun setupListeners() {
        binding.btnRedeemPoints.setOnClickListener {
            findNavController().navigate(R.id.action_rewardsFragment_to_redeemFragment)
        }
        binding.tvViewMore.setOnClickListener {
            viewModel.toggleExpanded()
        }
    }

    private fun setupRecyclerView() {
        adapter = ActivityAdapter()
        binding.rvRecentActivity.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loyaltyState.collect { state ->
                        binding.pointsBalanceVal.text = state.totalPoints.toString()
                        updateLoyaltyUi(state.stamps)
                    }
                }
                launch {
                    viewModel.filteredTransactions.collect { transactions ->
                        adapter.submitList(transactions)
                    }
                }
                launch {
                    viewModel.isExpanded.collect { expanded ->
                        binding.tvViewMore.text = if (expanded) "View Less" else "View More"
                    }
                }
                launch {
                    viewModel.transactions.collect { transactions ->
                        binding.tvViewMore.visibility = if (transactions.size > 5) View.VISIBLE else View.GONE
                    }
                }
            }
        }
    }

    private fun updateLoyaltyUi(stamps: Int) {
        val stampsContainer = binding.loyaltyVine.stampsContainer
        binding.loyaltyVine.tvStampsCount.text = "$stamps/8 Stamps"
        
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
