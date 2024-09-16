package com.hypersoft.baseproject.app.features.media.data.images.repository

import android.net.Uri
import com.hypersoft.baseproject.app.features.media.data.images.dataSources.MediaStoreMediaImages
import com.hypersoft.baseproject.app.features.media.domain.images.entities.ItemMediaImageFolder
import com.hypersoft.baseproject.app.features.media.domain.images.entities.ItemMediaImagePhoto

/**
 *   Developer: Sohaib Ahmed
 *   Date: 9/15/2024
 *   Profile:
 *     -> github.com/epegasus
 *     -> linkedin.com/in/epegasus
 */

class RepositoryMediaImages(private val mediaStoreEnhanceGallery: MediaStoreMediaImages) {

    fun getFolderNames(): List<ItemMediaImageFolder>? {
        return mediaStoreEnhanceGallery.getFolderNames()
    }

    fun getAllImages(): List<ItemMediaImagePhoto>? {
        return mediaStoreEnhanceGallery.getAllImages()
    }

    fun getImages(folderName: String): List<ItemMediaImagePhoto>? {
        return mediaStoreEnhanceGallery.getImages(folderName)
    }

    fun doesUriExist(imageUri: Uri): Boolean? {
        return mediaStoreEnhanceGallery.doesUriExist(imageUri)
    }
}