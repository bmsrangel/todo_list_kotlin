package br.com.bmsrangel.dev.todolist.app.core.repositories.storage

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject


class FirebaseStorageRepositoryImpl @Inject constructor(private val firebaseStorage: FirebaseStorage): StorageRepository {
    override suspend fun saveImageToStorage(userId: String, imageBitmap: Bitmap): Result<Uri> {
        val storageRef = firebaseStorage.reference
        val imageRef = storageRef.child("profile_images/$userId")
        val byteArrayOutputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()
        val uploadTask = imageRef.putBytes(data)
        val uploadTaskResult = uploadTask.await()
        return if (uploadTaskResult.error != null) {
            Result.failure(uploadTaskResult.error!!)
        } else {
            val imageDownloadUri = imageRef.downloadUrl.await()
            val url = Uri.parse(imageDownloadUri.toString())
            Result.success(url)
        }
    }
}