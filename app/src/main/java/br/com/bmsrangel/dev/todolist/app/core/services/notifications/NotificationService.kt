package br.com.bmsrangel.dev.todolist.app.core.services.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import br.com.bmsrangel.dev.todolist.R
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

const val notificationId = 0

class NotificationService @Inject constructor(): BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d("NOTIFICATION", "Notification Received")
        if (p0 != null) {
            val title = p1!!.getStringExtra("title")
            val message = p1.getStringExtra("message")
            sendNotification(p0, title!!, message!!)
        }
    }

    private fun sendNotification(context: Context, title: String, message: String) {
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(context, "TODO")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, notification.build())
    }
}