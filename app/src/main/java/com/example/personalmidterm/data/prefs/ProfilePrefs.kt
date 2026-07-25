package com.example.personalmidterm.data.prefs

import android.content.Context
import com.example.personalmidterm.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfilePrefs(context: Context) {
    private val prefs = context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)
    
    private val _profile = MutableStateFlow(readProfile())
    val profile: StateFlow<Profile> = _profile.asStateFlow()

    private fun readProfile(): Profile {
        return Profile(
            name = prefs.getString("name", "Xavier") ?: "Xavier",
            email = prefs.getString("email", "xavier@example.com") ?: "xavier@example.com",
            phone = prefs.getString("phone", "+1234567890") ?: "+1234567890",
            address = prefs.getString("address", "123 Coffee St, Bean City") ?: "123 Coffee St, Bean City"
        )
    }

    fun saveProfile(profile: Profile) {
        prefs.edit().apply {
            putString("name", profile.name)
            putString("email", profile.email)
            putString("phone", profile.phone)
            putString("address", profile.address)
            apply()
        }
        _profile.value = profile
    }
}
