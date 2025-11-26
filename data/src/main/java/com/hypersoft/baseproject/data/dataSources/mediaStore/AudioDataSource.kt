package com.hypersoft.baseproject.data.dataSources.mediaStore

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.hypersoft.baseproject.core.constants.Constants.GALLERY_UNKNOWN
import com.hypersoft.baseproject.domain.media.entities.AudioEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioDataSource(private val contentResolver: ContentResolver) {

    private val contentUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    suspend fun getAllAudios(): List<AudioEntity> = withContext(Dispatchers.IO) {
        val audios = mutableListOf<AudioEntity>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.BUCKET_DISPLAY_NAME
        )

        contentResolver.query(
            contentUri,
            projection,
            "${MediaStore.Audio.Media.IS_MUSIC} != 0",
            null,
            "${MediaStore.Audio.Media.DATE_MODIFIED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val audioUri = ContentUris.withAppendedId(contentUri, id)
                audios.add(
                    AudioEntity(
                        uri = audioUri,
                        id = id,
                        displayName = cursor.getString(nameColumn) ?: "",
                        size = cursor.getLong(sizeColumn),
                        duration = cursor.getLong(durationColumn),
                        artist = cursor.getString(artistColumn) ?: "",
                        album = cursor.getString(albumColumn) ?: "",
                        dateModified = cursor.getLong(dateColumn),
                        folderName = cursor.getString(folderColumn) ?: GALLERY_UNKNOWN
                    )
                )
            }
        }
        audios
    }
}