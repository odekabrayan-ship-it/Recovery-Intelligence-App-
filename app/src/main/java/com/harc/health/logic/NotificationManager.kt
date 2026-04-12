package com.harc.health.logic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.harc.health.MainActivity
import com.harc.health.R

object RecoveryNotificationManager {
    private const val CHANNEL_ID = "recovery_reminders"
    private const val CHANNEL_NAME = "Biological Directives"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Reminders for biological recovery and optimization protocols"
                enableLights(true)
                enableVibration(true)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendProtocolReminder(context: Context, title: String, message: String, sessionId: String? = null) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            if (sessionId != null) {
                putExtra("session_id", sessionId)
            } else {
                putExtra("navigate_to", "recovery")
            }
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, sessionId?.hashCode() ?: 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Should be replaced with a proper app icon
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(context.getColor(android.R.color.holo_blue_dark))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(title.hashCode(), builder.build())
    }
}
