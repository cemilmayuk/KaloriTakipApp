package com.cemilmayuk.kaloritakipapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "consumed_foods")
data class ConsumedFood(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val foodId: Int,
    val name: String,
    val calories: Double,
    val amount: Int,
    val unit: String,
    val date: Date = Date()
) 