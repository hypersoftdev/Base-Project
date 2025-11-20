package com.hypersoft.baseproject.domain.media.useCases

import com.hypersoft.baseproject.domain.media.entities.VideoEntity
import com.hypersoft.baseproject.domain.media.repositories.MediaRepository

class GetVideosUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(): List<VideoEntity> = repository.getAllVideos()
}