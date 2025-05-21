package com.cemilmayuk.kaloritakipapp.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class ConsumedFoodAdapter : RecyclerView.Adapter<ConsumedFoodAdapter.ViewHolder>() {
    private var foods: List<ConsumedFood> = emptyList()

    fun updateFoods(newFoods: List<ConsumedFood>) {
        foods = newFoods
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = foods[position]
        holder.bind(food)
    }

    override fun getItemCount() = foods.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val text1: TextView = view.findViewById(android.R.id.text1)
        private val text2: TextView = view.findViewById(android.R.id.text2)
        private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(food: ConsumedFood) {
            text1.text = "${food.name} - ${food.calories} kalori"
            text2.text = dateFormat.format(food.date)
        }
    }
} 