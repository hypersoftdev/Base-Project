package com.hypersoft.baseproject.data.dataSources.mediaStore

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.hypersoft.baseproject.core.constants.Constants.GALLERY_UNKNOWN
import com.hypersoft.baseproject.domain.media.entities.VideoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VideoDataSource(private val contentResolver: ContentResolver) {

    private val contentUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    suspend fun getAllVideos(): List<VideoEntity> = withContext(Dispatchers.IO) {
        val videos = mutableListOf<VideoEntity>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.WIDTH,
            MediaStore.Video.Media.HEIGHT,
            MediaStore.Video.Media.DATE_MODIFIED,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME
        )

        contentResolver.query(
            contentUri,
            projection,
            null,
            null,
            "${MediaStore.Video.Media.DATE_MODIFIED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.HEIGHT)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_MODIFIED)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val videoUri = ContentUris.withAppendedId(contentUri, id)
                videos.add(
                    VideoEntity(
                        uri = videoUri,
                        id = id,
                        displayName = cursor.getString(nameColumn) ?: "",
                        size = cursor.getLong(sizeColumn),
                        duration = cursor.getLong(durationColumn),
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                        dateModified = cursor.getLong(dateColumn),
                        folderName = cursor.getString(folderColumn) ?: GALLERY_UNKNOWN
                    )
                )
            }
        }
        videos
    }
}