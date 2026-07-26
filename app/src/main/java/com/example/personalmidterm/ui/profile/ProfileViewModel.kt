package com.example.personalmidterm.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalmidterm.data.prefs.LoyaltyPrefs
import com.example.personalmidterm.data.repository.ProfileRepository
import com.example.personalmidterm.model.Profile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val loyaltyPrefs: LoyaltyPrefs
) : ViewModel() {

    val profile: StateFlow<Profile> = profileRepository.profile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Profile("", "", "", "")
    )

    val loyaltyState = loyaltyPrefs.loyaltyState

    fun updateProfile(name: String, email: String, phone: String, address: String, imagePath: String? = null) {
        viewModelScope.launch {
            profileRepository.saveProfile(Profile(name, email, phone, address, imagePath))
        }
    }
}
