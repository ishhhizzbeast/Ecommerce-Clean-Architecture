package com.example.rushbuy.core.foundation.data.local.Migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // This migration handles the change of primary key from non-autoGenerate to autoGenerate.
        // Room often requires a full table recreation for such changes,
        // even if the SQLite column type (INTEGER PRIMARY KEY) remains the same.
        // The safest way is to rename the old table, create a new one, copy data, and drop the old.

        // 1. Create the new table with the desired schema (including autoGenerate = true implicitly handled by Room if id is INTEGER PRIMARY KEY)
        // Ensure this SQL matches the schema that Room would generate for your new ProductEntity.
        // It's helpful to generate the schema JSON and look at it (see "Export Schema" below).
        database.execSQL("""
            CREATE TABLE products_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                imageUrl TEXT NOT NULL,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                description TEXT NOT NULL,
                ratings REAL NOT NULL,
                category TEXT NOT NULL
            )
        """.trimIndent())

        // 2. Copy the data from the old table to the new table
        database.execSQL("""
            INSERT INTO products_new (id, imageUrl, name, price, description, ratings, category)
            SELECT id, imageUrl, name, price, description, ratings, category FROM products
        """.trimIndent())

        // 3. Drop the old table
        database.execSQL("DROP TABLE products")

        // 4. Rename the new table to the old table's name
        database.execSQL("ALTER TABLE products_new RENAME TO products")
    }
}