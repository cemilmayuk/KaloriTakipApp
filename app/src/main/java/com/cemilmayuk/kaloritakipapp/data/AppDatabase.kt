package com.cemilmayuk.kaloritakipapp.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Food::class, ConsumedFood::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao
    abstract fun consumedFoodDao(): ConsumedFoodDao

    companion object {
        private const val TAG = "AppDatabase"
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Food tablosunu yeniden oluştur
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `foods_new` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL,
                        `calories_per_100` REAL NOT NULL,
                        `unit` TEXT NOT NULL,
                        `category` TEXT NOT NULL DEFAULT 'Diğer'
                    )
                """)

                // Eski verileri yeni tabloya kopyala
                database.execSQL("""
                    INSERT INTO foods_new (id, name, calories_per_100, unit, category)
                    SELECT id, name, calories_per_100, unit, category FROM foods
                """)

                // Eski tabloyu sil
                database.execSQL("DROP TABLE foods")

                // Yeni tabloyu eski isimle yeniden adlandır
                database.execSQL("ALTER TABLE foods_new RENAME TO foods")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                try {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "food_database"
                    )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    INSTANCE = instance

                    // Veritabanı oluşturulduğunda verileri yükle
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val foodDao = instance.foodDao()
                            val foodList = FoodUtils.loadFoodDataFromAssets(context)
                            Log.d(TAG, "Assetten gelen veri sayısı: ${foodList.size}")
                            
                            if (foodList.isNotEmpty()) {
                                foodDao.insertAll(foodList)
                                Log.d(TAG, "Veriler veritabanına yüklendi")
                            } else {
                                Log.e(TAG, "Veri yüklenemedi veya liste boş!")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Veri yükleme hatası: ${e.message}", e)
                        }
                    }

                    instance
                } catch (e: Exception) {
                    Log.e(TAG, "Veritabanı oluşturma hatası: ${e.message}", e)
                    throw e
                }
            }
        }
    }
} 