package com.cemilmayuk.kaloritakipapp.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cemilmayuk.kaloritakipapp.data.ConsumedFood
import com.cemilmayuk.kaloritakipapp.data.FoodRepository
import kotlinx.coroutines.launch

class FoodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FoodRepository(application)

    private val _selectedFood = MutableLiveData<Food?>()
    val selectedFood: LiveData<Food?> = _selectedFood

    // Tüketilen yiyecekleri doğrudan repository'den LiveData olarak al
    val consumedFoods: LiveData<List<ConsumedFood>> = repository.getConsumedFoodsForToday()

    private val _dailyCalorieGoal = MutableLiveData<Int>(2000)
    val dailyCalorieGoal: LiveData<Int> = _dailyCalorieGoal

    init {
        // loadConsumedFoods() artık gerekli değil, consumedFoods LiveData'sı otomatik güncellenir
    }

    // loadConsumedFoods() metodu artık gerekli değil

    fun getCaloriesByName(name: String) {
        viewModelScope.launch {
            try {
                val food = repository.getFoodByName(name)
                _selectedFood.value = food
            } catch (e: Exception) {
                _selectedFood.value = null
            }
        }
    }

    fun addConsumedFood(food: Food, amount: Int) {
        viewModelScope.launch {
            try {
                repository.addConsumedFood(food, amount)
                // Yiyecek eklendiğinde consumedFoods LiveData'sı otomatik güncellenir
                // loadConsumedFoods() burada gerekli değil
            } catch (e: Exception) {
                // Hata durumunda işlem yapma
            }
        }
    }

    fun setDailyCalorieGoal(goal: Int) {
        _dailyCalorieGoal.value = goal
    }

    fun getTotalConsumedCalories(): Double {
        return consumedFoods.value?.sumOf { it.calories } ?: 0.0
    }

    fun getRemainingCalories(): Double {
        return (_dailyCalorieGoal.value ?: 0) - getTotalConsumedCalories()
    }
} 