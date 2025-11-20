package com.hypersoft.baseproject.domain.di

import com.hypersoft.baseproject.domain.media.repositories.MediaRepository
import com.hypersoft.baseproject.domain.media.useCases.GetAudiosUseCase
import com.hypersoft.baseproject.domain.media.useCases.GetImageFoldersUseCase
import com.hypersoft.baseproject.domain.media.useCases.GetImagesUseCase
import com.hypersoft.baseproject.domain.media.useCases.GetVideosUseCase
import com.hypersoft.baseproject.domain.media.useCases.contentObserver.ObserveMediaChangesUseCase
import org.koin.dsl.lazyModule

val domainModule = lazyModule {
    factory { GetAudiosUseCase(get<MediaRepository>()) }

    factory { GetImageFoldersUseCase(get<MediaRepository>()) }
    factory { GetImagesUseCase(get<MediaRepository>()) }
    factory { GetVideosUseCase(get<MediaRepository>()) }

    factory { ObserveMediaChangesUseCase(get<MediaRepository>()) }
}