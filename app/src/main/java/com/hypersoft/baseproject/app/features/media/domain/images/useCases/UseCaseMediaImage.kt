package com.hypersoft.baseproject.app.features.media.domain.images.useCases

import com.hypersoft.baseproject.app.features.media.data.images.repository.RepositoryMediaImages
import com.hypersoft.baseproject.app.features.media.domain.images.entities.ItemMediaImageFolder

/**
 *   Developer: Sohaib Ahmed
 *   Date: 9/15/2024
 *   Profile:
 *     -> github.com/epegasus
 *     -> linkedin.com/in/epegasus
 */

class UseCaseMediaImage(private val repositoryMediaImages: RepositoryMediaImages) {

    /**
     * Sorted by Ascending order
     */

    fun getFolderNames(): List<ItemMediaImageFolder>? {
        return repositoryMediaImages.getFolderNames()
    }
}