package com.cemilmayuk.kaloritakipapp.ui.selectfood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cemilmayuk.kaloritakipapp.R
import com.cemilmayuk.kaloritakipapp.data.Food

class FoodAdapter(
    private val onFoodClick: (Food) -> Unit
) : ListAdapter<Any, RecyclerView.ViewHolder>(FoodDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_CATEGORY = 0
        private const val VIEW_TYPE_FOOD = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FoodCategory -> VIEW_TYPE_CATEGORY
            is Food -> VIEW_TYPE_FOOD
            else -> throw IllegalArgumentException("Bilinmeyen öğe tipi")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CATEGORY -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_header, parent, false)
                CategoryViewHolder(view)
            }
            VIEW_TYPE_FOOD -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_food, parent, false)
                FoodViewHolder(view, onFoodClick)
            }
            else -> throw IllegalArgumentException("Bilinmeyen görünüm tipi")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryViewHolder -> {
                val category = getItem(position) as FoodCategory
                holder.bind(category)
            }
            is FoodViewHolder -> {
                val food = getItem(position) as Food
                holder.bind(food)
            }
        }
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)

        fun bind(category: FoodCategory) {
            categoryName.text = category.categoryName
        }
    }

    class FoodViewHolder(
        itemView: View,
        private val onFoodClick: (Food) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val foodName: TextView = itemView.findViewById(R.id.foodName)
        private val foodCalories: TextView = itemView.findViewById(R.id.foodCalories)

        fun bind(food: Food) {
            foodName.text = food.name
            foodCalories.text = "${food.calories_per_100} kcal/100${food.unit}"
            itemView.setOnClickListener { onFoodClick(food) }
        }
    }
}

class FoodDiffCallback : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is FoodCategory && newItem is FoodCategory -> 
                oldItem.categoryName == newItem.categoryName
            oldItem is Food && newItem is Food -> 
                oldItem.id == newItem.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return when {
            oldItem is FoodCategory && newItem is FoodCategory -> 
                oldItem == newItem
            oldItem is Food && newItem is Food -> 
                oldItem == newItem
            else -> false
        }
    }
} 