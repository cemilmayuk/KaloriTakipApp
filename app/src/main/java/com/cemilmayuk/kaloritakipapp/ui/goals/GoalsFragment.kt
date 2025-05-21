package com.cemilmayuk.kaloritakipapp.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cemilmayuk.kaloritakipapp.data.NutritionGoals
import com.cemilmayuk.kaloritakipapp.databinding.FragmentGoalsBinding

class GoalsFragment : Fragment() {
    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.nutritionGoals.observe(viewLifecycleOwner) { goals ->
            binding.caloriesInput.setText(goals.calories.toString())
        }

        viewModel.updateSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(context, "Hedef başarıyla kaydedildi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Hedef kaydedilirken bir hata oluştu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            val caloriesText = binding.caloriesInput.text.toString()
            if (caloriesText.isNotEmpty()) {
                try {
                    val calories = caloriesText.toInt()
                    if (calories > 0) {
                        viewModel.updateGoals(calories)
                    } else {
                        Toast.makeText(context, "Lütfen geçerli bir hedef girin", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Toast.makeText(context, "Lütfen geçerli bir sayı girin", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Lütfen bir hedef girin", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 