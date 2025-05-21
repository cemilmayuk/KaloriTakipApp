package com.cemilmayuk.kaloritakipapp.ui.addfood

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.cemilmayuk.kaloritakipapp.data.Food
import com.cemilmayuk.kaloritakipapp.data.FoodRepository
import kotlinx.coroutines.launch

class AddFoodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FoodRepository(application)

    // Yiyecek adları listesi
    private val _foodNames = MutableLiveData<List<String>>()
    val foodNames: LiveData<List<String>> = _foodNames

    // Seçilen yiyecek
    private val _selectedFood = MutableLiveData<Food?>()
    val selectedFood: LiveData<Food?> = _selectedFood

    // Ekle butonu durumu
    private val _isAddButtonEnabled = MutableLiveData(false)
    val isAddButtonEnabled: LiveData<Boolean> = _isAddButtonEnabled

    private val _addFoodResult = MutableLiveData<Boolean>()
    val addFoodResult: LiveData<Boolean> = _addFoodResult

    init {
        loadFoodNames()
    }

    // Yiyecek adlarını yükle
    private fun loadFoodNames() {
        viewModelScope.launch {
            try {
                val names = repository.getAllFoodNames()
                Log.d("FOOD_DEBUG", "Yüklenen yiyecek adları: ${names.take(5)}")
                _foodNames.value = names
            } catch (e: Exception) {
                Log.e("FOOD_DEBUG", "Yiyecek adları yüklenirken hata: ${e.message}")
                _foodNames.value = emptyList()
            }
        }
    }

    // Yiyecek ara ve seç
    fun searchAndSelectFood(query: String) {
        viewModelScope.launch {
            try {
                val trimmedQuery = query.trim()
                Log.d("FOOD_DEBUG", "Arama sorgusu: '$trimmedQuery'")

                if (trimmedQuery.isBlank()) {
                    Log.d("FOOD_DEBUG", "Boş sorgu, seçim temizleniyor")
                    clearSelection()
                    return@launch
                }

                // Önce tam eşleşme ara
                val exactMatch = repository.getFoodByNameExact(trimmedQuery)
                Log.d("FOOD_DEBUG", "Tam eşleşme sonucu: ${exactMatch?.name}")

                if (exactMatch != null) {
                    Log.d("FOOD_DEBUG", "Tam eşleşme bulundu: ${exactMatch.name}")
                    _selectedFood.value = exactMatch
                } else {
                    // Tam eşleşme yoksa, başlayan eşleşme ara
                    val partialMatch = repository.getFoodByName(trimmedQuery)
                    Log.d("FOOD_DEBUG", "Kısmi eşleşme sonucu: ${partialMatch?.name}")

                    if (partialMatch != null) {
                        Log.d("FOOD_DEBUG", "Kısmi eşleşme bulundu: ${partialMatch.name}")
                        _selectedFood.value = partialMatch
                    } else {
                        Log.d("FOOD_DEBUG", "Hiçbir eşleşme bulunamadı")
                        clearSelection()
                    }
                }
            } catch (e: Exception) {
                Log.e("FOOD_DEBUG", "Arama sırasında hata: ${e.message}")
                clearSelection()
            }
        }
    }

    // Seçimi temizle
    fun clearSelection() {
        _selectedFood.value = null
        _isAddButtonEnabled.value = false
    }

    // Ekle butonu durumunu güncelle
    fun updateAddButtonState(amount: Int? = null) {
        val food = _selectedFood.value
        _isAddButtonEnabled.value = food != null && amount != null && amount > 0
    }

    // Yiyeceği ekle
    fun addFood(amount: Int) {
        val food = _selectedFood.value ?: return
        viewModelScope.launch {
            try {
                repository.addConsumedFood(food, amount)
                _addFoodResult.value = true
            } catch (e: Exception) {
                Log.e("FOOD_DEBUG", "Yiyecek eklenirken hata: ${e.message}")
                _addFoodResult.value = false
            }
        }
    }
} 