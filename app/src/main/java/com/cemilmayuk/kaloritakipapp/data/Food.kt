package com.cemilmayuk.kaloritakipapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "foods")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    @SerializedName("calories_per_100")
    val calories_per_100: Double,
    val unit: String,
    val category: String = "Diğer"
) {
    // JSON'dan okuma için constructor
    constructor(id: Int, name: String, calories_per_100: Double, unit: String) : this(
        id = id,
        name = name,
        calories_per_100 = calories_per_100,
        unit = unit,
        category = "Diğer"
    )
} 