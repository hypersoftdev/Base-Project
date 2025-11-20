package com.hypersoft.baseproject.data.di

import com.hypersoft.baseproject.data.dataSources.inAppMemory.history.HistoryDataSource
import com.hypersoft.baseproject.data.dataSources.inAppMemory.languages.LanguageDataSource
import com.hypersoft.baseproject.data.dataSources.mediaStore.AudioDataSource
import com.hypersoft.baseproject.data.dataSources.mediaStore.ImageDataSource
import com.hypersoft.baseproject.data.dataSources.mediaStore.VideoDataSource
import com.hypersoft.baseproject.data.dataSources.mediaStore.contentObservers.MediaStoreObserver
import com.hypersoft.baseproject.data.dataSources.storage.SharedPrefManager
import com.hypersoft.baseproject.data.repositories.entrance.EntranceRepository
import com.hypersoft.baseproject.data.repositories.entrance.EntranceRepositoryImpl
import com.hypersoft.baseproject.data.repositories.history.HistoryRepository
import com.hypersoft.baseproject.data.repositories.history.HistoryRepositoryImpl
import com.hypersoft.baseproject.data.repositories.language.LanguageRepository
import com.hypersoft.baseproject.data.repositories.language.LanguageRepositoryImpl
import com.hypersoft.baseproject.data.repositories.media.MediaRepositoryImpl
import com.hypersoft.baseproject.domain.media.repositories.MediaRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.lazyModule

val dataSourceModules = lazyModule {
    // InAppMemory
    single { LanguageDataSource() }
    single { HistoryDataSource() }

    // MediaStore
    single { MediaStoreObserver(androidContext().contentResolver) }
    single { AudioDataSource(androidContext().contentResolver) }
    single { ImageDataSource(androidContext().contentResolver) }
    single { VideoDataSource(androidContext().contentResolver) }

    // Storage
    single { SharedPrefManager(androidContext()) }
}

val repositoryModules = lazyModule {
    single<EntranceRepository> { EntranceRepositoryImpl(get(), get()) }
    single<LanguageRepository> { LanguageRepositoryImpl(get(), get(), get()) }
    single<HistoryRepository> { HistoryRepositoryImpl(get(), get()) }
    single<MediaRepository> { MediaRepositoryImpl(get(), get(), get(), get(), get()) }
}