package com.hypersoft.baseproject.domain.media.useCases

import com.hypersoft.baseproject.core.constants.Constants.GALLERY_ALL
import com.hypersoft.baseproject.domain.media.entities.ImageEntity
import com.hypersoft.baseproject.domain.media.repositories.MediaRepository

class GetImagesUseCase(
    private val repository: MediaRepository
) {
    suspend operator fun invoke(folderName: String? = null): List<ImageEntity> {
        return if (folderName == null || folderName == GALLERY_ALL) {
            repository.getAllImages()
        } else {
            repository.getImagesByFolder(folderName)
        }
    }
}