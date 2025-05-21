package com.cemilmayuk.kaloritakipapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cemilmayuk.kaloritakipapp.data.ConsumedFood
import com.cemilmayuk.kaloritakipapp.databinding.ItemConsumedFoodBinding
import java.text.SimpleDateFormat
import java.util.*

class ConsumedFoodAdapter(
    private val onDeleteClick: (ConsumedFood) -> Unit
) : ListAdapter<ConsumedFood, ConsumedFoodAdapter.ConsumedFoodViewHolder>(ConsumedFoodDiffCallback()) {

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsumedFoodViewHolder {
        val binding = ItemConsumedFoodBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConsumedFoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConsumedFoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ConsumedFoodViewHolder(
        private val binding: ItemConsumedFoodBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(consumedFood: ConsumedFood) {
            binding.apply {
                foodNameText.text = consumedFood.name
                foodCaloriesText.text = "${consumedFood.calories.toInt()} kcal"
                foodAmountText.text = "${consumedFood.amount} ${consumedFood.unit}"
                deleteButton.setOnClickListener {
                    onDeleteClick(consumedFood)
                }
            }
        }
    }

    private class ConsumedFoodDiffCallback : DiffUtil.ItemCallback<ConsumedFood>() {
        override fun areItemsTheSame(oldItem: ConsumedFood, newItem: ConsumedFood): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ConsumedFood, newItem: ConsumedFood): Boolean {
            return oldItem == newItem
        }
    }
} 