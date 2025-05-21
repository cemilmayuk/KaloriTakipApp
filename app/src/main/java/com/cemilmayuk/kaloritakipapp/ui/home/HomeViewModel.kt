package com.cemilmayuk.kaloritakipapp.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cemilmayuk.kaloritakipapp.data.ConsumedFood
import com.cemilmayuk.kaloritakipapp.data.FoodRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "HomeViewModel"
    private val repository = FoodRepository(application)

    val consumedFoods: LiveData<List<ConsumedFood>> = repository.getConsumedFoodsForToday()

    private val _dailyGoal = MutableLiveData<Int>()
    val dailyGoal: LiveData<Int> = _dailyGoal

    private val _totalCalories = MediatorLiveData<Double>().apply {
        addSource(consumedFoods) { foods ->
            value = foods?.sumOf { it.calories } ?: 0.0
        }
    }
    val totalCalories: LiveData<Double> = _totalCalories

    private val _remainingCalories = MediatorLiveData<Double>().apply {
        addSource(_totalCalories) { total ->
            val goal = _dailyGoal.value?.toDouble() ?: 0.0
            value = goal - total
        }
        addSource(_dailyGoal) { goal ->
            val total = _totalCalories.value ?: 0.0
            value = goal.toDouble() - total
        }
    }
    val remainingCalories: LiveData<Double> = _remainingCalories

    init {
        Log.d(TAG, "HomeViewModel init")
        // Başlangıç değerini yükle
        _dailyGoal.value = repository.getNutritionGoals().calories
    }

    fun refreshDailyGoal() {
        Log.d(TAG, "Günlük hedef yenileniyor")
        _dailyGoal.value = repository.getNutritionGoals().calories
    }

    fun deleteConsumedFood(consumedFood: ConsumedFood) {
        viewModelScope.launch {
            try {
                repository.deleteConsumedFood(consumedFood)
            } catch (e: Exception) {
                Log.e(TAG, "Yiyecek silinirken hata: ${e.message}")
            }
        }
    }
} 