package com.example.personalmidterm.ui.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.prefs.LoyaltyPrefs
import com.example.personalmidterm.data.repository.RewardRepository
import com.example.personalmidterm.model.RewardTransaction
import kotlinx.coroutines.flow.*

class RewardsViewModel(
    private val rewardRepository: RewardRepository,
    private val loyaltyPrefs: LoyaltyPrefs
) : ViewModel() {

    val transactions: StateFlow<List<RewardTransaction>> = rewardRepository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _isExpanded = MutableStateFlow(false)
    val isExpanded: StateFlow<Boolean> = _isExpanded.asStateFlow()

    val filteredTransactions: StateFlow<List<RewardTransaction>> = 
        combine(transactions, _isExpanded) { list, expanded ->
            if (expanded) list else list.take(5)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val loyaltyState = loyaltyPrefs.loyaltyState

    fun toggleExpanded() {
        _isExpanded.value = !_isExpanded.value
    }
}
