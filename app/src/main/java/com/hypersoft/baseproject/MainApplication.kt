package com.hypersoft.baseproject

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.hypersoft.baseproject.helpers.firebase.EventsProvider
import com.hypersoft.baseproject.helpers.firebase.FirebaseUtils.postFirebaseEvent
import com.hypersoft.baseproject.helpers.koin.modulesList
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@MainApplication)
            modules(modulesList)
        }
    }
}