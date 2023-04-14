package br.com.bmsrangel.dev.todolist.app.core.services.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationService: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("NOTIFICATION", "Notification Received")
    }
}