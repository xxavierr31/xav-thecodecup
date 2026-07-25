package com.example.personalmidterm.data.repository

import com.example.personalmidterm.data.prefs.ProfilePrefs
import com.example.personalmidterm.model.Profile
import kotlinx.coroutines.flow.StateFlow

class ProfileRepository(private val profilePrefs: ProfilePrefs) {
    val profile: StateFlow<Profile> = profilePrefs.profile

    fun saveProfile(profile: Profile) {
        profilePrefs.saveProfile(profile)
    }
}
