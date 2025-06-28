package com.example.rushbuy.feature.notification.presentation.util

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.rushbuy.MainActivity
import com.example.rushbuy.feature.notification.common.Constants

class NotificationHelper(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        Log.d("NotificationHelper", "NotificationHelper initialized. Creating channels.")
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val newItemChannel = NotificationChannel(
                Constants.NEW_ITEM_NOTIFICATION_CHANNEL_ID,
                "New Item Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for newly added items by admin"
                enableLights(true)
                lightColor = context.getColor(R.color.holo_red_light) // Verify this color exists!
                enableVibration(true)
            }
            val foregroundServiceChannel = NotificationChannel(
                Constants.FOREGROUND_SERVICE_CHANNEL_ID,
                "App Background Services",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Indicates ongoing app tasks in the background (e.g., data sync)"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(newItemChannel)
            notificationManager.createNotificationChannel(foregroundServiceChannel)
            Log.d("NotificationHelper", "Notification channels created/updated.")
        } else {
            Log.d("NotificationHelper", "Notification channels not created (API < O).")
        }
    }

    fun createForegroundServiceNotification(): Notification {
        Log.d("NotificationHelper", "Building foreground service notification.")
        return NotificationCompat.Builder(context, Constants.FOREGROUND_SERVICE_CHANNEL_ID)
            .setContentTitle("RushBuy Active")
            .setContentText("Performing essential app operations...")
            .setSmallIcon(R.drawable.ic_notification_overlay) // Verify this icon exists!
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build().also {
                Log.d("NotificationHelper", "Foreground service notification built.")
            }
    }

    fun showNewItemNotification(itemId: Int, itemName: String) {
        Log.d("NotificationHelper", "Attempting to show new item notification for ID: $itemId, Name: $itemName")
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            Constants.NEW_ITEM_NOTIFICATION_ID,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, Constants.NEW_ITEM_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_overlay) // Verify this icon exists!
            .setContentTitle("New Item Added!")
            .setContentText("New Item just added: $itemName")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Admin just added a new item called '$itemName'. Check it out!"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        try {
            notificationManager.notify(Constants.NEW_ITEM_NOTIFICATION_ID, notification)
            Log.d("NotificationHelper", "Notification for ID $itemId posted successfully to NotificationManager.")
        } catch (e: Exception) {
            Log.e("NotificationHelper", "CRITICAL ERROR posting notification for ID $itemId: ${e.message}", e)
        }
    }
}