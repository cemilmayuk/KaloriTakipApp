package com.cemilmayuk.kaloritakipapp.ui.goals

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cemilmayuk.kaloritakipapp.data.FoodRepository
import com.cemilmayuk.kaloritakipapp.data.NutritionGoals
import kotlinx.coroutines.launch

class GoalsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FoodRepository(application)
    private val TAG = "GoalsViewModel"

    private val _nutritionGoals = MutableLiveData<NutritionGoals>()
    val nutritionGoals: LiveData<NutritionGoals> = _nutritionGoals

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    init {
        Log.d(TAG, "init çağrıldı")
        loadGoals()
    }

    private fun loadGoals() {
        Log.d(TAG, "loadGoals çağrıldı")
        try {
            val goals = repository.getNutritionGoals()
            Log.d(TAG, "Hedefler yüklendi: ${goals.calories}")
            _nutritionGoals.value = goals
        } catch (e: Exception) {
            Log.e(TAG, "Hedefler yüklenirken hata: ${e.message}")
            _nutritionGoals.value = NutritionGoals(2000)
        }
    }

    fun updateGoals(calories: Int) {
        Log.d(TAG, "updateGoals çağrıldı: $calories")
        viewModelScope.launch {
            try {
                val success = repository.updateDailyCalorieGoal(calories)
                if (success) {
                    _nutritionGoals.value = NutritionGoals(calories)
                    _updateSuccess.value = true
                    Log.d(TAG, "Hedefler başarıyla güncellendi")
                } else {
                    _updateSuccess.value = false
                    Log.e(TAG, "Hedefler güncellenemedi")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Hedefler güncellenirken hata: ${e.message}")
                _updateSuccess.value = false
            }
        }
    }
} 