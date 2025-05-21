package com.cemilmayuk.kaloritakipapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _someData = MutableLiveData<String>()
    val someData: LiveData<String> = _someData

    // Modern LiveData.map kullanımı
    val transformedData: LiveData<String> = someData.map { value ->
        // Dönüşüm işlemleri burada yapılır
        value.uppercase()
    }

    fun updateData(newValue: String) {
        viewModelScope.launch {
            _someData.value = newValue
        }
    }
} 