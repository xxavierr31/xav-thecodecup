package com.example.personalmidterm.data.prefs

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class LoyaltyState(
    val stamps: Int,
    val totalPoints: Int,
    val rankIndex: Int
)

class LoyaltyPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("loyalty_prefs", Context.MODE_PRIVATE)

    private val _loyaltyState = MutableStateFlow(readLoyaltyState())
    val loyaltyState: StateFlow<LoyaltyState> = _loyaltyState.asStateFlow()

    private fun readLoyaltyState(): LoyaltyState {
        return LoyaltyState(
            stamps = prefs.getInt("stamps", 0),
            totalPoints = prefs.getInt("totalPoints", 0),
            rankIndex = prefs.getInt("rankIndex", 0)
        )
    }

    fun updateLoyalty(stamps: Int, totalPoints: Int, rankIndex: Int) {
        prefs.edit().apply {
            putInt("stamps", stamps)
            putInt("totalPoints", totalPoints)
            putInt("rankIndex", rankIndex)
            apply()
        }
        _loyaltyState.value = LoyaltyState(stamps, totalPoints, rankIndex)
    }
}
