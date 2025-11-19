package com.hypersoft.baseproject.di

import com.hypersoft.baseproject.core.di.modules.coreModule
import com.hypersoft.baseproject.core.di.modules.dispatchersModule
import com.hypersoft.baseproject.di.modules.appModule
import com.hypersoft.baseproject.presentation.entrance.di.entrancePresentationModule
import com.hypersoft.baseproject.presentation.language.di.languagePresentationModule
import com.hypersoft.baseproject.presentation.settings.di.settingsPresentationModule
import org.koin.core.module.LazyModule

class KoinModules {

    fun getKoinModules(): List<LazyModule> {
        return listOf(
            // App Modules
            appModule,

            // Core Modules
            coreModule,
            dispatchersModule,

            /*// Data Modules
            dataSourceModule,
            entranceDataModule,
            languageDataModule,
            audioDataModule,*/

            // Presentation Modules
            entrancePresentationModule,
            languagePresentationModule,
            settingsPresentationModule,
        )
    }
}