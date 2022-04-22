package com.wildraion.taskmanager.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.wildraion.taskmanager.R

const val channelID = "TaskManagerChannel"
const val titleExtra = "titleExtra"
const val messageExtra = "messageExtra"
const val notificationIDExtra = "notificationIDExtra"

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val notification = NotificationCompat.Builder(context!!, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent!!.getStringExtra(titleExtra))
            .setContentText(intent.getStringExtra(messageExtra))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(100, 1000, 200, 340))
            .setAutoCancel(false)
            .build()

        val notificationID = intent.getIntExtra(notificationIDExtra, 1)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationID, notification)
    }
}