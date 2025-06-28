package com.example.rushbuy.feature.notification.di

import com.example.rushbuy.feature.notification.domain.usecase.TriggerNewItemNotificationUseCase
import com.example.rushbuy.feature.notification.presentation.util.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val  NotificationModule = module {
    single { NotificationHelper(context = androidContext()) }
    single { TriggerNewItemNotificationUseCase(context = androidContext()) }
}