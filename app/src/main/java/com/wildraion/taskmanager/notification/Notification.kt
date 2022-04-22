package com.wildraion.taskmanager.notification

import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log


class Notification (private var id: Int, private val title: String,
                    private val message: String, private val time: Long)
{

    fun scheduleNotification(context: Context){
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.putExtra(notificationIDExtra, id)
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time,
            pendingIntent)

        Log.w("Notification schedule", "$title | $message")
    }

    fun removeNotification(context: Context){
        val myIntent = Intent(context, NotificationReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            myIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        Log.w("Notification canceled", "$id | $title | $message")
    }
}