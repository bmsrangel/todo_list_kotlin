package br.com.bmsrangel.dev.todolist.app.core.services.messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AppMessagingService: FirebaseMessagingService() {
    @Inject
    lateinit var firebaseMessaging: FirebaseMessaging
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Refreshed Token", token)
    }

    suspend fun getCurrentToken(): String? {
        return firebaseMessaging.token.await()
    }
}