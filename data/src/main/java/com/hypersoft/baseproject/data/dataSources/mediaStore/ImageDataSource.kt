package com.hypersoft.baseproject.data.dataSources.mediaStore

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.hypersoft.baseproject.core.constants.Constants.GALLERY_ALL
import com.hypersoft.baseproject.core.constants.Constants.GALLERY_UNKNOWN
import com.hypersoft.baseproject.domain.media.entities.ImageEntity
import com.hypersoft.baseproject.domain.media.entities.ImageFolderEntity

class ImageDataSource(private val contentResolver: ContentResolver) {

    private val contentUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    fun getAllFolders(): List<ImageFolderEntity> {
        val folders = mutableSetOf<ImageFolderEntity>()
        val projection = arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        contentResolver.query(
            contentUri,
            projection,
            null,
            null,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )?.use { cursor ->
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val folderName = cursor.getString(folderColumn) ?: GALLERY_UNKNOWN
                folders.add(ImageFolderEntity(folderName = folderName))
            }
        }
        return folders.toList()
    }

    fun getAllImages(): List<ImageEntity> {
        val images = mutableListOf<ImageEntity>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        contentResolver.query(
            contentUri,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val imageUri = ContentUris.withAppendedId(contentUri, id)
                images.add(
                    ImageEntity(
                        uri = imageUri,
                        id = id,
                        displayName = cursor.getString(nameColumn) ?: "",
                        size = cursor.getLong(sizeColumn),
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                        dateModified = cursor.getLong(dateColumn),
                        folderName = cursor.getString(folderColumn) ?: GALLERY_UNKNOWN
                    )
                )
            }
        }
        return images
    }

    fun getImagesByFolder(folderName: String): List<ImageEntity> {
        val images = mutableListOf<ImageEntity>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.DATE_MODIFIED,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val selection = if (folderName == GALLERY_ALL) {
            null
        } else {
            "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        }

        val selectionArgs = if (folderName == GALLERY_ALL) {
            null
        } else {
            arrayOf(folderName)
        }

        contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val imageUri = ContentUris.withAppendedId(contentUri, id)
                images.add(
                    ImageEntity(
                        uri = imageUri,
                        id = id,
                        displayName = cursor.getString(nameColumn) ?: "",
                        size = cursor.getLong(sizeColumn),
                        width = cursor.getInt(widthColumn),
                        height = cursor.getInt(heightColumn),
                        dateModified = cursor.getLong(dateColumn),
                        folderName = cursor.getString(folderColumn) ?: GALLERY_UNKNOWN
                    )
                )
            }
        }
        return images
    }
}