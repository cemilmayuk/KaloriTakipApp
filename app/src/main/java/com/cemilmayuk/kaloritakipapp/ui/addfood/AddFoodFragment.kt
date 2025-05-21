package com.cemilmayuk.kaloritakipapp.ui.addfood

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.cemilmayuk.kaloritakipapp.R
import com.cemilmayuk.kaloritakipapp.data.Food
import com.cemilmayuk.kaloritakipapp.databinding.FragmentAddFoodBinding

class AddFoodFragment : Fragment() {
    private var _binding: FragmentAddFoodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddFoodViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAutoComplete()
        observeViewModel()
        setupAmountInput()
        setupAddButton()
    }

    private fun setupAutoComplete() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown,
            mutableListOf<String>()
        )
        binding.foodNameInput.setAdapter(adapter)

        viewModel.foodNames.observe(viewLifecycleOwner) { names ->
            Log.d("FOOD_FRAGMENT", "Yiyecek adları yüklendi: ${names.take(5)}")
            adapter.clear()
            adapter.addAll(names)
            adapter.notifyDataSetChanged()
        }

        binding.foodNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val input = s?.toString()?.trim()
                Log.d("FOOD_FRAGMENT", "Metin değişti: '$input'")
                if (!input.isNullOrEmpty()) {
                    viewModel.searchAndSelectFood(input)
                } else {
                    viewModel.clearSelection()
                }
            }
        })

        binding.foodNameInput.setOnItemClickListener { _, _, position, _ ->
            val selectedFoodName = adapter.getItem(position)
            Log.d("FOOD_FRAGMENT", "Öğe seçildi: $selectedFoodName")
            selectedFoodName?.let { name ->
                viewModel.searchAndSelectFood(name)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.selectedFood.observe(viewLifecycleOwner) { food ->
            Log.d("FOOD_FRAGMENT", "Seçilen yiyecek değişti: ${food?.name}")
            if (food != null) {
                binding.foodDetailsCard.visibility = View.VISIBLE
                binding.caloriesText.text = "Kalori: ${food.calories_per_100} kcal/100${food.unit}"
                binding.amountLayout.hint = "Miktar (${food.unit})"
                calculateTotalCalories(food)
            } else {
                binding.foodDetailsCard.visibility = View.GONE
                binding.caloriesText.text = "Kalori: --"
                binding.amountLayout.hint = "Miktar"
            }
            val amount = binding.amountInput.text.toString().toIntOrNull()
            viewModel.updateAddButtonState(amount)
        }

        viewModel.isAddButtonEnabled.observe(viewLifecycleOwner) { isEnabled ->
            Log.d("FOOD_FRAGMENT", "Buton durumu değişti: $isEnabled")
            if (isEnabled) {
                enableAddButton()
            } else {
                disableAddButton()
            }
        }

        viewModel.addFoodResult.observe(viewLifecycleOwner) { success ->
            Log.d("FOOD_FRAGMENT", "Ekleme sonucu: $success")
            if (success) {
                Toast.makeText(
                    context,
                    "Yiyecek başarıyla eklendi",
                    Toast.LENGTH_SHORT
                ).show()
                clearInputs()
            } else {
                Toast.makeText(
                    context,
                    "Yiyecek eklenirken bir hata oluştu",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupAmountInput() {
        binding.amountInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val amount = s?.toString()?.toIntOrNull()
                Log.d("FOOD_FRAGMENT", "Miktar değişti: $amount")
                viewModel.updateAddButtonState(amount)
                viewModel.selectedFood.value?.let { calculateTotalCalories(it) }
            }
        })
    }

    private fun calculateTotalCalories(food: Food) {
        val amount = binding.amountInput.text.toString().toIntOrNull()
        if (amount != null && amount > 0) {
            val totalCalories = when (food.unit.lowercase()) {
                "gram", "ml" -> (amount * food.calories_per_100) / 100
                else -> amount * food.calories_per_100
            }
            binding.caloriesText.text = "Kalori: ${food.calories_per_100} kcal/100${food.unit} (Toplam: ${String.format("%.1f", totalCalories)} kcal)"
        } else {
            binding.caloriesText.text = "Kalori: ${food.calories_per_100} kcal/100${food.unit}"
        }
    }

    private fun setupAddButton() {
        binding.addButton.setOnClickListener {
            val amount = binding.amountInput.text.toString().toIntOrNull()
            Log.d("FOOD_FRAGMENT", "Ekle butonuna tıklandı, miktar: $amount")
            if (amount != null && amount > 0) {
                viewModel.addFood(amount)
            } else {
                Toast.makeText(
                    context,
                    "Lütfen geçerli bir miktar girin",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun clearInputs() {
        binding.amountInput.text?.clear()
        binding.foodNameInput.text?.clear()
        viewModel.clearSelection()
    }

    private fun enableAddButton() {
        binding.addButton.isEnabled = true
        binding.addButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
    }

    private fun disableAddButton() {
        binding.addButton.isEnabled = false
        binding.addButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 