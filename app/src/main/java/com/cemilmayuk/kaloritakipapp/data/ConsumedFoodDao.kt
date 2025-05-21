package com.cemilmayuk.kaloritakipapp.data

import androidx.room.*
import androidx.lifecycle.LiveData
import java.util.Date

@Dao
interface ConsumedFoodDao {
    @Insert
    suspend fun insert(consumedFood: ConsumedFood): Long

    @Query("SELECT * FROM consumed_foods WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getConsumedFoodsForDateRange(startDate: Date, endDate: Date): LiveData<List<ConsumedFood>>

    @Query("SELECT SUM(calories) FROM consumed_foods WHERE date >= :startDate AND date <= :endDate")
    suspend fun getTotalCaloriesForDateRange(startDate: Date, endDate: Date): Double?

    @Delete
    suspend fun delete(consumedFood: ConsumedFood)
} 