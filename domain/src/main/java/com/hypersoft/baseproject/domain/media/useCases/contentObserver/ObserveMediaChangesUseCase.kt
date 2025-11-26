package com.hypersoft.baseproject.domain.media.useCases.contentObserver

import com.hypersoft.baseproject.domain.media.repositories.MediaRepository
import kotlinx.coroutines.flow.Flow

class ObserveMediaChangesUseCase(private val repository: MediaRepository) {
    fun observeAll(): Flow<Unit> = repository.observeAllMediaChanges()
    fun observeAudios(): Flow<Unit> = repository.observeAudiosChanges()
    fun observeImages(): Flow<Unit> = repository.observeImagesChanges()
    fun observeVideos(): Flow<Unit> = repository.observeVideosChanges()
}