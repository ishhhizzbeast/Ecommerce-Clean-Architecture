package com.example.rushbuy.feature.notification.domain.usecase

import android.content.Context
import android.util.Log
import com.example.rushbuy.feature.notification.domain.model.NewItemNotificationData
import com.example.rushbuy.feature.notification.presentation.service.NewItemNotificationService

class TriggerNewItemNotificationUseCase(
    private val context: Context // Context is needed to start the service
) {
    operator fun invoke(data: NewItemNotificationData) {
        Log.d("NotificationUseCase", "Triggering notification service for ID: ${data.id}, Name: ${data.name}")
        NewItemNotificationService.startService(context, data.id, data.name)
    }
}