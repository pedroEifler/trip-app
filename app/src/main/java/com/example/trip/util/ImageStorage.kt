package com.example.trip.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Stores trip photos inside the app's internal storage (filesDir/trip_photos) so the
 * reference saved in the database keeps working even if the original gallery item is removed.
 */
object ImageStorage {

    private const val DIR_NAME = "trip_photos"

    private fun photosDir(context: Context): File =
        File(context.filesDir, DIR_NAME).apply { if (!exists()) mkdirs() }

    /** Creates an empty destination file for the camera to write the captured image into. */
    fun createImageFile(context: Context): File =
        File(photosDir(context), "IMG_${System.currentTimeMillis()}.jpg")

    /** Wraps a file into a content URI shared with the camera app through the FileProvider. */
    fun getUriForFile(context: Context, file: File): Uri =
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

    /** Copies a picked gallery image (content URI) into internal storage and returns the new file. */
    suspend fun copyUriToInternalStorage(context: Context, uri: Uri): File =
        withContext(Dispatchers.IO) {
            val destination = createImageFile(context)
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destination).use { output ->
                    input.copyTo(output)
                }
            }
            destination
        }
}

