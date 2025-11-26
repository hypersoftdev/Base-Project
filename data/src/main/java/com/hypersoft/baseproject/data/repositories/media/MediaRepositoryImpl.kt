package com.hypersoft.baseproject.data.repositories.media

import com.hypersoft.baseproject.data.dataSources.mediaStore.AudioDataSource
import com.hypersoft.baseproject.data.dataSources.mediaStore.ImageDataSource
import com.hypersoft.baseproject.data.dataSources.mediaStore.VideoDataSource
import com.hypersoft.baseproject.data.dataSources.mediaStore.contentObservers.MediaStoreObserver
import com.hypersoft.baseproject.domain.media.entities.AudioEntity
import com.hypersoft.baseproject.domain.media.entities.ImageEntity
import com.hypersoft.baseproject.domain.media.entities.ImageFolderEntity
import com.hypersoft.baseproject.domain.media.entities.VideoEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import com.hypersoft.baseproject.domain.media.repositories.MediaRepository as DomainMediaRepository

class MediaRepositoryImpl(
    private val mediaStoreObserver: MediaStoreObserver,
    private val audioDataSource: AudioDataSource,
    private val imageDataSource: ImageDataSource,
    private val videoDataSource: VideoDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : DomainMediaRepository {

    override fun observeAllMediaChanges(): Flow<Unit> = mediaStoreObserver.observeAllMediaChanges()
    override fun observeAudiosChanges(): Flow<Unit> = mediaStoreObserver.observeAudiosChanges()
    override fun observeImagesChanges(): Flow<Unit> = mediaStoreObserver.observeImagesChanges()
    override fun observeVideosChanges(): Flow<Unit> = mediaStoreObserver.observeVideosChanges()

    override suspend fun getAllAudios(): List<AudioEntity> = withContext(ioDispatcher) { audioDataSource.getAllAudios() }

    override suspend fun getAllImageFolders(): List<ImageFolderEntity> = withContext(ioDispatcher) { imageDataSource.getAllFolders() }
    override suspend fun getAllImages(): List<ImageEntity> = withContext(ioDispatcher) { imageDataSource.getAllImages() }
    override suspend fun getImagesByFolder(folderName: String): List<ImageEntity> = withContext(ioDispatcher) { imageDataSource.getImagesByFolder(folderName) }

    override suspend fun getAllVideos(): List<VideoEntity> = withContext(ioDispatcher) { videoDataSource.getAllVideos() }
}