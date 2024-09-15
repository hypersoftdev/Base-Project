package com.hypersoft.baseproject.app.features.media.domain.images.useCases

import android.net.Uri
import com.hypersoft.baseproject.app.features.media.data.images.repository.RepositoryMediaImages
import com.hypersoft.baseproject.app.features.media.domain.images.entities.ItemMediaImagePhoto
import com.hypersoft.baseproject.utilities.utils.ConstantUtils

/**
 *   Developer: Sohaib Ahmed
 *   Date: 9/15/2024
 *   Profile:
 *     -> github.com/epegasus
 *     -> linkedin.com/in/epegasus
 */

class UseCaseMediaImageDetail(private val repositoryMediaImages: RepositoryMediaImages) {

    /**
     * Sorted by Descending order
     */

    fun getImages(folderName: String): List<ItemMediaImagePhoto>? {
        val shouldGetAllImages = folderName.equals(ConstantUtils.GALLERY_ALL, true)
        return when (shouldGetAllImages) {
            true -> repositoryMediaImages.getAllImages()
            false -> repositoryMediaImages.getImages(folderName)
        }
    }

    fun doesUriExist(imageUri: Uri): Boolean? {
        return repositoryMediaImages.doesUriExist(imageUri)
    }
}