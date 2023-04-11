package br.com.bmsrangel.dev.todolist.app.core.repositories.storage

import android.graphics.Bitmap
import android.net.Uri
import java.io.File

interface StorageRepository {
    suspend fun saveImageToStorage(userId: String, imageBitmap: Bitmap): Result<Uri>
    fun downloadAndSaveLocalImage(imageUrl: String, imageDirectory: String): File
}