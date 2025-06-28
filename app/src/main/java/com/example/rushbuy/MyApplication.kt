package com.example.rushbuy

import android.app.Application
import com.example.rushbuy.core.foundation.di.coreModule
import com.example.rushbuy.feature.admin.di.adminModule
import com.example.rushbuy.feature.auth.di.authModule
import com.example.rushbuy.feature.notification.di.NotificationModule
import com.example.rushbuy.feature.splash.di.splashModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
    super.onCreate()
    startKoin {
        androidLogger()
        androidContext(this@MyApplication)
        modules(splashModule, authModule, coreModule, adminModule, NotificationModule)
    }
}
}
