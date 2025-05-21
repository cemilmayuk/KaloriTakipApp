package com.cemilmayuk.kaloritakipapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<Food>)

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :foodName || '%' COLLATE NOCASE")
    suspend fun getCaloriesByName(foodName: String): Food?

    @Query("SELECT * FROM foods WHERE name LIKE :foodName COLLATE NOCASE")
    suspend fun getFoodByNameExact(foodName: String): Food?

    @Query("SELECT DISTINCT name FROM foods ORDER BY name ASC")
    suspend fun getAllFoodNames(): List<String>

    @Query("SELECT * FROM foods WHERE category LIKE :category ORDER BY name ASC")
    suspend fun getFoodsByCategory(category: String): List<Food>

    @Query("SELECT DISTINCT category FROM foods ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>
} 