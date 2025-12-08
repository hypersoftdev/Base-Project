package com.hypersoft.baseproject.domain.media.useCases

import com.hypersoft.baseproject.domain.media.entities.AudioEntity
import com.hypersoft.baseproject.domain.media.repositories.MediaRepository

class GetAudiosUseCase(private val repository: MediaRepository) {
    suspend operator fun invoke(): List<AudioEntity> = repository.getAllAudios()
}