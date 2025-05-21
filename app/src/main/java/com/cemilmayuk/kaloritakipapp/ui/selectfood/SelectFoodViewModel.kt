package com.cemilmayuk.kaloritakipapp.ui.selectfood

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cemilmayuk.kaloritakipapp.data.Food
import com.cemilmayuk.kaloritakipapp.data.FoodRepository
import kotlinx.coroutines.launch

data class FoodCategory(
    val categoryName: String,
    val foods: List<Food>
)

class SelectFoodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FoodRepository(application)

    private val _foodCategories = MutableLiveData<List<FoodCategory>>()
    val foodCategories: LiveData<List<FoodCategory>> = _foodCategories

    private val _selectedFood = MutableLiveData<Food?>()
    val selectedFood: LiveData<Food?> = _selectedFood

    private val _allFoods = MutableLiveData<List<Food>>()
    val allFoods: LiveData<List<Food>> = _allFoods

    init {
        loadFoods()
    }

    private fun loadFoods() {
        viewModelScope.launch {
            try {
                val categories = repository.getAllCategories()
                val foodsByCategory = categories.map { category ->
                    val foods = repository.getFoodsByCategory(category)
                    FoodCategory(category, foods)
                }
                _foodCategories.value = foodsByCategory
                _allFoods.value = foodsByCategory.flatMap { it.foods }
            } catch (e: Exception) {
                _foodCategories.value = emptyList()
                _allFoods.value = emptyList()
            }
        }
    }

    fun filterFoods(query: String) {
        viewModelScope.launch {
            try {
                val allFoods = _allFoods.value ?: return@launch
                if (query.isEmpty()) {
                    loadFoods()
                    return@launch
                }

                val filteredFoods = allFoods.filter { food ->
                    food.name.contains(query, ignoreCase = true)
                }

                val categories = repository.getAllCategories()
                val foodsByCategory = categories.map { category ->
                    val categoryFoods = filteredFoods.filter { it.category == category }
                    FoodCategory(category, categoryFoods)
                }.filter { it.foods.isNotEmpty() }

                _foodCategories.value = foodsByCategory
            } catch (e: Exception) {
                _foodCategories.value = emptyList()
            }
        }
    }

    fun selectFood(food: Food) {
        _selectedFood.value = food
    }

    fun addFood(food: Food) {
        viewModelScope.launch {
            try {
                repository.addConsumedFood(food, 1) // Varsayılan miktar 1
            } catch (e: Exception) {
                // Hata durumunda işlem yapma
            }
        }
    }
} 