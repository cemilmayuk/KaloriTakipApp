package com.cemilmayuk.kaloritakipapp.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class FoodRepository(application: Application) {
    private val TAG = "FoodRepository"
    private val foodDao = AppDatabase.getDatabase(application).foodDao()
    private val consumedFoodDao = AppDatabase.getDatabase(application).consumedFoodDao()
    private val context = application.applicationContext
    private val sharedPreferences = application.getSharedPreferences("nutrition_prefs", Application.MODE_PRIVATE)
    private val gson = Gson()

    // Günlük kalori hedefi için LiveData
    private val _dailyCalorieGoal = MutableLiveData<Int>()
    val dailyCalorieGoal: LiveData<Int> = _dailyCalorieGoal

    init {
        loadDailyCalorieGoal()
    }

    private fun loadDailyCalorieGoal() {
        val savedGoal = sharedPreferences.getInt("daily_calorie_goal", 2000)
        _dailyCalorieGoal.value = savedGoal
    }

    suspend fun getAllFoodNames(): List<String> = withContext(Dispatchers.IO) {
        try {
            val names = foodDao.getAllFoodNames()
            Log.d("FOOD_REPO", "Tüm yiyecek adları yüklendi: ${names.take(5)}")
            names
        } catch (e: Exception) {
            Log.e("FOOD_REPO", "Yiyecek adları yüklenirken hata: ${e.message}")
            emptyList()
        }
    }

    suspend fun getFoodByName(name: String): Food? = withContext(Dispatchers.IO) {
        try {
            val food = foodDao.getCaloriesByName(name)
            Log.d("FOOD_REPO", "Kısmi eşleşme araması: '$name' -> ${food?.name}")
            food
        } catch (e: Exception) {
            Log.e("FOOD_REPO", "Kısmi eşleşme araması sırasında hata: ${e.message}")
            null
        }
    }

    suspend fun getFoodByNameExact(name: String): Food? = withContext(Dispatchers.IO) {
        try {
            val food = foodDao.getFoodByNameExact(name)
            Log.d("FOOD_REPO", "Tam eşleşme araması: '$name' -> ${food?.name}")
            food
        } catch (e: Exception) {
            Log.e("FOOD_REPO", "Tam eşleşme araması sırasında hata: ${e.message}")
            null
        }
    }

    suspend fun getFoodsByCategory(category: String): List<Food> = withContext(Dispatchers.IO) {
        try {
            foodDao.getFoodsByCategory(category)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllCategories(): List<String> = withContext(Dispatchers.IO) {
        try {
            foodDao.getAllCategories()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addConsumedFood(food: Food, amount: Int) = withContext(Dispatchers.IO) {
        try {
            if (amount <= 0) {
                throw IllegalArgumentException("Miktar 0'dan büyük olmalıdır")
            }

            val consumedFood = ConsumedFood(
                foodId = food.id,
                name = food.name,
                calories = calculateCalories(food, amount),
                amount = amount,
                unit = food.unit,
                date = Date()
            )
            Log.d("FOOD_REPO", "Yiyecek ekleniyor: ${food.name}, miktar: $amount")
            val result = consumedFoodDao.insert(consumedFood)
            Log.d("FOOD_REPO", "ConsumedFood ekleme sonucu (satır id): $result")

        } catch (e: Exception) {
            Log.e("FOOD_REPO", "Yiyecek eklenirken hata: ${e.message}")
            throw e
        }
    }

    private fun calculateCalories(food: Food, amount: Int): Double {
        return when (food.unit.lowercase()) {
            "gram", "ml" -> (amount * food.calories_per_100) / 100.0
            else -> amount * food.calories_per_100.toDouble()
        }
    }

    fun getConsumedFoodsForToday(): LiveData<List<ConsumedFood>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val startDate = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endDate = calendar.time

        return consumedFoodDao.getConsumedFoodsForDateRange(startDate, endDate)
    }

    suspend fun getTotalCaloriesForToday(): Double = withContext(Dispatchers.IO) {
        try {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            val startDate = calendar.time

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endDate = calendar.time

            consumedFoodDao.getTotalCaloriesForDateRange(startDate, endDate) ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }

    suspend fun deleteConsumedFood(consumedFood: ConsumedFood) = withContext(Dispatchers.IO) {
        try {
            consumedFoodDao.delete(consumedFood)
        } catch (e: Exception) {
            throw e
        }
    }

    fun getNutritionGoals(): NutritionGoals {
        val calories = sharedPreferences.getInt("daily_calorie_goal", 2000)
        return NutritionGoals(calories)
    }

    fun updateNutritionGoals(): Boolean {
        return try {
            val goals = getNutritionGoals()
            sharedPreferences.edit().putInt("daily_calorie_goal", goals.calories).apply()
            _dailyCalorieGoal.value = goals.calories
            true
        } catch (e: Exception) {
            Log.e(TAG, "Hedefler güncellenirken hata: ${e.message}")
            false
        }
    }

    fun updateDailyCalorieGoal(calories: Int): Boolean {
        return try {
            sharedPreferences.edit().putInt("daily_calorie_goal", calories).apply()
            _dailyCalorieGoal.value = calories
            true
        } catch (e: Exception) {
            Log.e(TAG, "Günlük kalori hedefi güncellenirken hata: ${e.message}")
            false
        }
    }
} 