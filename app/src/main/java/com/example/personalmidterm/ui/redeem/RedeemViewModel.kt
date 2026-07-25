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
    private val loyaltyPrefs: LoyaltyPrefs
) : ViewModel() {

    val redeemables: StateFlow<List<RedeemableItem>> = redeemableRepository.allRedeemables.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val loyaltyState = loyaltyPrefs.loyaltyState

    fun redeem(item: RedeemableItem) {
        viewModelScope.launch {
            val state = loyaltyState.value
            if (state.totalPoints >= item.pointsCost) {
                loyaltyPrefs.updateLoyalty(
                    stamps = state.stamps,
                    totalPoints = state.totalPoints - item.pointsCost,
                    rankIndex = state.rankIndex
                )
                // In a real app, we'd also record a transaction and give the user a coupon
            }
        }
    }
}
