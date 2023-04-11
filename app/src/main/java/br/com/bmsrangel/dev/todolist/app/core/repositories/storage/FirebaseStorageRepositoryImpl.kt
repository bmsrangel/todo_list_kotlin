package br.com.bmsrangel.dev.todolist.app.core.repositories.storage

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
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

    override fun downloadAndSaveLocalImage(imageUrl: String, imageDirectory: String): File {
        val url = URL(imageUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val input = connection.inputStream
        val dir = File(imageDirectory)
        if (!dir.exists()) {
            dir.mkdir()
        }
        val file = File(dir, "profile.jpg")
        val output = FileOutputStream(file)
        val buffer = ByteArray(1024 )
        var read: Int
        while(input.read(buffer).also { read = it } != -1) {
            output.write(buffer, 0, read)
        }
        output.flush()
        output.close()
        input.close()

        return file
    }
}