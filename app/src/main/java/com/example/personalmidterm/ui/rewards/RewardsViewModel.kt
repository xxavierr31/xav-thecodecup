package com.example.personalmidterm.ui.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.prefs.LoyaltyPrefs
import com.example.personalmidterm.data.repository.RewardRepository
import com.example.personalmidterm.model.RewardTransaction
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RewardsViewModel(
    private val rewardRepository: RewardRepository,
    private val loyaltyPrefs: LoyaltyPrefs
) : ViewModel() {

    val transactions: StateFlow<List<RewardTransaction>> = rewardRepository.allTransactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val loyaltyState = loyaltyPrefs.loyaltyState
}
