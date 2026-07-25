package com.example.personalmidterm

import android.app.Application
import com.example.personalmidterm.di.AppContainer
import com.example.personalmidterm.di.AppDataContainer

class CodeCupApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
