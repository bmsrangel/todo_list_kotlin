package br.com.bmsrangel.dev.todolist.app.core.repositories.storage

import android.graphics.Bitmap
import android.net.Uri

interface StorageRepository {
    suspend fun saveImageToStorage(userId: String, imageBitmap: Bitmap): Result<Uri>
}