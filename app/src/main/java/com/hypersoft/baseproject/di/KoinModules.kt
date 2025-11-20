package com.hypersoft.baseproject.di

import com.hypersoft.baseproject.core.di.modules.coreModule
import com.hypersoft.baseproject.core.di.modules.dispatchersModule
import com.hypersoft.baseproject.data.di.dataSourceModules
import com.hypersoft.baseproject.data.di.repositoryModules
import com.hypersoft.baseproject.di.modules.appModule
import com.hypersoft.baseproject.domain.di.domainModule
import com.hypersoft.baseproject.presentation.entrance.di.entrancePresentationModule
import com.hypersoft.baseproject.presentation.history.di.historyPresentationModule
import com.hypersoft.baseproject.presentation.home.di.homePresentationModule
import com.hypersoft.baseproject.presentation.inAppLanguage.di.inAppLanguagePresentationModule
import com.hypersoft.baseproject.presentation.language.di.languagePresentationModule
import com.hypersoft.baseproject.presentation.media.di.mediaPresentationModule
import com.hypersoft.baseproject.presentation.mediaAudios.di.mediaAudiosPresentationModule
import com.hypersoft.baseproject.presentation.mediaAudioDetails.di.mediaAudioDetailsPresentationModule
import com.hypersoft.baseproject.presentation.premium.di.premiumPresentationModule
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

            // Data Modules
            dataSourceModules,
            repositoryModules,

            // Domain Modules
            domainModule,

            // Presentation Modules
            entrancePresentationModule,
            languagePresentationModule,

            homePresentationModule,
            mediaPresentationModule,
            mediaAudiosPresentationModule,
            mediaAudioDetailsPresentationModule,
            historyPresentationModule,

            premiumPresentationModule,
            settingsPresentationModule,
            inAppLanguagePresentationModule,
        )
    }
}