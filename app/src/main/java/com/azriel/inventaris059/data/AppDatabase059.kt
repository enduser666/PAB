package com.azriel.inventaris059.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [User059::class, Item059::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase059 : RoomDatabase() {

    abstract fun userDao059(): UserDao059
    abstract fun itemDao059(): ItemDao059

    companion object {
        @Volatile
        private var INSTANCE059: AppDatabase059? = null

        val MIGRATION_1_2_059 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE item_059 ADD COLUMN fotoPath059 TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE item_059 ADD COLUMN deskripsi059 TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE item_059 ADD COLUMN kategori059 TEXT DEFAULT NULL")
            }
        }

        fun getInstance059(context: Context): AppDatabase059 {
            return INSTANCE059 ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase059::class.java,
                    "inventaris059.db"
                )
                    .addMigrations(MIGRATION_1_2_059)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE059 = it }
            }
        }
    }
}
