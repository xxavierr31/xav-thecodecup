package com.example.personalmidterm.ui.redeem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.prefs.LoyaltyPrefs
import com.example.personalmidterm.data.repository.RedeemableRepository
import com.example.personalmidterm.model.RedeemableItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RedeemViewModel(
    private val redeemableRepository: RedeemableRepository,
    private val rewardRepository: com.example.personalmidterm.data.repository.RewardRepository,
    private val loyaltyPrefs: LoyaltyPrefs
) : ViewModel() {

    val redeemables: StateFlow<List<RedeemableItem>> = redeemableRepository.allRedeemables.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val loyaltyState = loyaltyPrefs.loyaltyState

    val canRedeemAnything: StateFlow<Boolean> = kotlinx.coroutines.flow.combine(
        redeemables,
        loyaltyState
    ) { items, state ->
        items.any { state.totalPoints >= it.pointsCost }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun redeem(item: RedeemableItem) {
        viewModelScope.launch {
            rewardRepository.redeemPoints(item.pointsCost, "Redeemed: ${item.name}")
        }
    }
}
