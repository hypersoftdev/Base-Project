package com.hypersoft.baseproject.domain.media.repositories

import com.hypersoft.baseproject.domain.media.entities.AudioEntity
import com.hypersoft.baseproject.domain.media.entities.ImageEntity
import com.hypersoft.baseproject.domain.media.entities.ImageFolderEntity
import com.hypersoft.baseproject.domain.media.entities.VideoEntity
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun observeAllMediaChanges(): Flow<Unit>
    fun observeAudiosChanges(): Flow<Unit>
    fun observeImagesChanges(): Flow<Unit>
    fun observeVideosChanges(): Flow<Unit>

    suspend fun getAllAudios(): List<AudioEntity>
    suspend fun getAllImageFolders(): List<ImageFolderEntity>
    suspend fun getAllImages(): List<ImageEntity>

    suspend fun getImagesByFolder(folderName: String): List<ImageEntity>

    suspend fun getAllVideos(): List<VideoEntity>
}