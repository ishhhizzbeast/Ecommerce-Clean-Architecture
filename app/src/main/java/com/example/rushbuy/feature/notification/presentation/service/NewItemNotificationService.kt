package com.example.rushbuy.feature.notification.presentation.service


import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.rushbuy.feature.notification.common.Constants // Keep this for channel IDs
import com.example.rushbuy.feature.notification.presentation.util.NotificationHelper
import org.koin.android.ext.android.inject

class NewItemNotificationService : Service() {

    private val notificationHelper: NotificationHelper by inject()

    companion object {
        const val EXTRA_ITEM_ID = "extra_item_id"
        const val EXTRA_ITEM_NAME = "extra_item_name"

        fun startService(context: Context, itemId: Int, itemName: String) {
            Log.d("NotificationService", "Attempting to start service from Companion for ID: $itemId, Name: $itemName")
            val intent = Intent(context, NewItemNotificationService::class.java).apply {
                putExtra(EXTRA_ITEM_ID, itemId)
                putExtra(EXTRA_ITEM_NAME, itemName)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    ContextCompat.startForegroundService(context, intent)
                    Log.d("NotificationService", "Called ContextCompat.startForegroundService for ID: $itemId")
                } catch (e: Exception) {
                    Log.e("NotificationService", "Error calling ContextCompat.startForegroundService for ID $itemId: ${e.message}", e)
                }
            } else {
                try {
                    context.startService(intent)
                    Log.d("NotificationService", "Called context.startService for ID: $itemId (pre-Oreo)")
                } catch (e: Exception) {
                    Log.e("NotificationService", "Error calling context.startService for ID $itemId: ${e.message}", e)
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("NotificationService", "onStartCommand received for startId: $startId")
        val itemId = intent?.getIntExtra(EXTRA_ITEM_ID, 0) ?: 0
        val itemName = intent?.getStringExtra(EXTRA_ITEM_NAME) ?: "Unknown Item"
        Log.d("NotificationService", "Service received data in onStartCommand: ID=$itemId, Name=$itemName")

        try {
            val foregroundNotification = notificationHelper.createForegroundServiceNotification()
            Log.d("NotificationService", "Foreground notification created.")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    Constants.FOREGROUND_SERVICE_NOTIFICATION_ID,
                    foregroundNotification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                )
            } else {
                startForeground(Constants.FOREGROUND_SERVICE_NOTIFICATION_ID, foregroundNotification)
            }
            Log.d("NotificationService", "Service started in foreground.")

            if (itemId != 0) {
                Log.d("NotificationService", "Displaying new item notification (ID=$itemId).")
                notificationHelper.showNewItemNotification(itemId, itemName)
            } else {
                Log.w("NotificationService", "Item ID is 0 or invalid. Not displaying specific item notification.")
            }

        } catch (e: Exception) {
            Log.e("NotificationService", "Error during onStartCommand execution: ${e.message}", e)
        } finally {
            stopSelf()
            Log.d("NotificationService", "Service stopping itself after processing.")
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("NotificationService", "Service onDestroy called.")
    }
}