package com.example.rushbuy.core.foundation.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.rushbuy.core.foundation.data.local.dao.ProductDao
import com.example.rushbuy.core.foundation.data.local.entity.ProductEntity

@Database(entities = [ProductEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}
