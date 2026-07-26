package com.example.personalmidterm.ui.myorders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.personalmidterm.R
import com.example.personalmidterm.databinding.FragmentOrdersBinding
import com.example.personalmidterm.model.OrderStatus
import com.example.personalmidterm.ui.ViewModelFactory
import kotlinx.coroutines.launch

class MyOrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyOrdersViewModel by viewModels { ViewModelFactory.Factory }
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupHeader()
        setupTabs()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupHeader() {
        binding.header.screenTitle.text = "My Orders"
        binding.header.screenTitle.visibility = View.VISIBLE
        binding.header.backButton.visibility = View.GONE
        binding.header.favoriteIcon.visibility = View.GONE
        binding.header.cartIcon.visibility = View.GONE
    }

    private fun setupTabs() {
        binding.tabOngoing.setOnClickListener {
            viewModel.setStatus(OrderStatus.ONGOING)
        }
        binding.tabHistory.setOnClickListener {
            viewModel.setStatus(OrderStatus.HISTORY)
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter { order ->
            if (order.status == OrderStatus.ONGOING) {
                viewModel.completeOrder(order.id)
            }
        }
        binding.rvOrders.adapter = adapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.filteredOrders.collect { orders ->
                        adapter.submitList(orders)
                    }
                }
                launch {
                    viewModel.selectedStatus.collect { status ->
                        updateTabUi(status)
                    }
                }
                launch {
                    viewModel.orderCompletionEvent.collect { result ->
                        if (result.stamps == 0 && result.rankUpOccurred) {
                            android.widget.Toast.makeText(requireContext(), "8 Stamps collected! Your vine has fully regrown its leaves!", android.widget.Toast.LENGTH_LONG).show()
                            (activity as? com.example.personalmidterm.MainActivity)?.showRankUpNotification(result.newRankName)
                        } else if (result.stamps > 0) {
                            android.widget.Toast.makeText(requireContext(), "Stamp collected! ${result.stamps}/8", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun updateTabUi(status: OrderStatus) {
        val brown = requireContext().getColor(R.color.theme_brown)
        val grey = requireContext().getColor(R.color.text_secondary)
        
        val bold = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), R.font.playfairdisplay_bold)
        val medium = androidx.core.content.res.ResourcesCompat.getFont(requireContext(), R.font.playfairdisplay_medium)

        if (status == OrderStatus.ONGOING) {
            binding.tabOngoing.setTextColor(brown)
            binding.tabOngoing.typeface = bold
            binding.tabOngoingIndicator.visibility = View.VISIBLE
            
            binding.tabHistory.setTextColor(grey)
            binding.tabHistory.typeface = medium
            binding.tabHistoryIndicator.visibility = View.INVISIBLE
        } else {
            binding.tabOngoing.setTextColor(grey)
            binding.tabOngoing.typeface = medium
            binding.tabOngoingIndicator.visibility = View.INVISIBLE
            
            binding.tabHistory.setTextColor(brown)
            binding.tabHistory.typeface = bold
            binding.tabHistoryIndicator.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
