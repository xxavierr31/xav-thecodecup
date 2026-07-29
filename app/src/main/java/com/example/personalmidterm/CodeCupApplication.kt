package com.example.personalmidterm

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.personalmidterm.di.AppContainer
import com.example.personalmidterm.di.AppDataContainer

class CodeCupApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Force Light Mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        
        container = AppDataContainer(this)
    }
}
