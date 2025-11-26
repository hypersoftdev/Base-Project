package com.hypersoft.baseproject.domain.media.useCases

import com.hypersoft.baseproject.domain.media.entities.ImageFolderEntity
import com.hypersoft.baseproject.domain.media.repositories.MediaRepository

class GetImageFoldersUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(): List<ImageFolderEntity> = repository.getAllImageFolders()
}