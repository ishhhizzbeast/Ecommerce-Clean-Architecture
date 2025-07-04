package com.example.rushbuy.core.foundation.di

import androidx.room.Room
import com.example.rushbuy.core.foundation.data.local.Migration.MIGRATION_1_2
import com.example.rushbuy.core.foundation.data.local.database.AppDatabase
import com.example.rushbuy.core.foundation.data.local.source.IProductLocalDataSource
import com.example.rushbuy.core.foundation.data.local.source.ProductLocalDataSourceImpl
import com.example.rushbuy.core.foundation.data.remote.api.ProductApiService
import com.example.rushbuy.core.foundation.data.remote.source.IProductRemoteDataSource
import com.example.rushbuy.core.foundation.data.remote.source.ProductRemoteDataSourceImpl
import com.example.rushbuy.core.foundation.domain.repository.IProductRepository
import com.example.rushbuy.core.foundation.repository.ProductRepositoryImpl
import com.example.rushbuy.feature.admin.domain.GetAdminProductsUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val coreModule = module {

    // --- Room Database Dependencies ---
    single {
        Room.databaseBuilder(
            androidApplication(), // Koin's way to get the Application context
            AppDatabase::class.java,
            "rushbuy_db" // Database name
        )
            // Ensure MIGRATION_1_2 is correctly imported and defined in your AppDatabase or Mappers
            .addMigrations(MIGRATION_1_2)
            // .fallbackToDestructiveMigration() // Use this carefully for dev, removes data on schema changes
            .build()
    }

    // ProductDao (gets it from the AppDatabase instance)
    single { get<AppDatabase>().productDao() }


    // --- Retrofit and API Service Dependencies ---
    single {
        Retrofit.Builder()
            .baseUrl("https://dummyjson.com/") // Base URL for your API
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson for JSON parsing
            .build()
    }

    // ProductApiService (gets Retrofit instance)
    single { get<Retrofit>().create(ProductApiService::class.java) }


    // --- NEW: Data Source Dependencies ---
    // Providing IProductLocalDataSource with its concrete implementation ProductLocalDataSourceImpl
    single<IProductLocalDataSource> { ProductLocalDataSourceImpl(productDao = get()) }

    // Providing IProductRemoteDataSource with its concrete implementation ProductRemoteDataSourceImpl
    single<IProductRemoteDataSource> { ProductRemoteDataSourceImpl(apiService = get()) }


    // --- UPDATED: Repository Dependencies ---
    // Providing IProductRepository with its concrete implementation ProductRepositoryImpl
    // Koin now injects IProductRemoteDataSource and IProductLocalDataSource
    single<IProductRepository> {
        ProductRepositoryImpl(
            remoteDataSource = get(), // Koin automatically provides IProductRemoteDataSource
            localDataSource = get()   // Koin automatically provides IProductLocalDataSource
        )
    }
}