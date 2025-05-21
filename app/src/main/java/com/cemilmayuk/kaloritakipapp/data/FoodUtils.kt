package com.cemilmayuk.kaloritakipapp.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object FoodUtils {
    private const val TAG = "FoodUtils"

    private fun parseNutritionValues(name: String): Triple<Double, Double, Double> {
        // İsimden besin değerlerini çıkar
        val parts = name.split(" ")
        if (parts.size >= 5) {
            try {
                // Örnek: "Bıldırcın gr 19,6 0 12,1 -"
                // parts[0] = "Bıldırcın"
                // parts[1] = "gr"
                // parts[2] = "19,6" (protein)
                // parts[3] = "0" (carbs)
                // parts[4] = "12,1" (fat)
                val protein = parts[2].replace(",", ".").toDoubleOrNull() ?: 0.0
                val carbs = parts[3].replace(",", ".").toDoubleOrNull() ?: 0.0
                val fat = parts[4].replace(",", ".").toDoubleOrNull() ?: 0.0
                return Triple(protein, carbs, fat)
            } catch (e: Exception) {
                Log.e(TAG, "Besin değerleri parse edilemedi: $name", e)
            }
        }
        return Triple(0.0, 0.0, 0.0)
    }

    fun loadFoodDataFromAssets(context: Context): List<Food> {
        return try {
            // JSON dosyasını assets'ten oku
            val jsonString = try {
                context.assets
                    .open("food_data.json")
                    .bufferedReader()
                    .use { it.readText() }
            } catch (e: IOException) {
                Log.e(TAG, "JSON dosyası okunamadı: ${e.message}", e)
                return emptyList()
            }

            if (jsonString.isBlank()) {
                Log.e(TAG, "JSON içeriği boş!")
                return emptyList()
            }

            Log.d(TAG, "JSON içeriği yüklendi")

            // Gson ile JSON'ı List<Food>'a dönüştür
            val type = object : TypeToken<List<Food>>() {}.type
            val foodList = try {
                Gson().fromJson<List<Food>>(jsonString, type)
            } catch (e: Exception) {
                Log.e(TAG, "JSON parse hatası: ${e.message}", e)
                return emptyList()
            }

            // Liste kontrolü
            if (foodList.isNullOrEmpty()) {
                Log.e(TAG, "Parse edilen liste boş!")
                return emptyList()
            }

            // Kategori alanı boş olanları işle
            val processedFoodList = foodList.map { food ->
                if (food.category.isNullOrBlank()) {
                    Log.w(TAG, "Kategori alanı boş olan yiyecek bulundu: ${food.name}. Varsayılan 'Bilinmiyor' atanıyor.")
                    food.copy(category = "Bilinmiyor")
                } else {
                    food
                }
            }

            Log.d(TAG, "Başarıyla parse edildi. Liste uzunluğu: ${processedFoodList.size}")
            if (processedFoodList.isNotEmpty()) {
                Log.d(TAG, "İlk öğe: ${processedFoodList.first()}")
            }

            processedFoodList
        } catch (e: Exception) {
            Log.e(TAG, "Beklenmeyen hata: ${e.message}", e)
            emptyList()
        }
    }
} 