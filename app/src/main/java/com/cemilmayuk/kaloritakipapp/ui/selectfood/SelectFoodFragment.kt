package com.cemilmayuk.kaloritakipapp.ui.selectfood

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cemilmayuk.kaloritakipapp.data.Food
import com.cemilmayuk.kaloritakipapp.databinding.FragmentSelectFoodBinding

class SelectFoodFragment : Fragment() {
    private var _binding: FragmentSelectFoodBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SelectFoodViewModel by viewModels()
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeViewModel()
        setupAddButton()
    }

    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter { food ->
            viewModel.selectFood(food)
        }
        binding.foodRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = foodAdapter
        }
    }

    private fun setupSearch() {
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.filterFoods(s?.toString() ?: "")
            }
        })
    }

    private fun observeViewModel() {
        viewModel.foodCategories.observe(viewLifecycleOwner) { categories ->
            foodAdapter.submitList(categories)
        }

        viewModel.selectedFood.observe(viewLifecycleOwner) { food ->
            food?.let {
                binding.selectedFoodCard.visibility = View.VISIBLE
                binding.selectedFoodName.text = it.name
                binding.selectedFoodDetails.text = "Kalori: ${it.calories_per_100} kcal/100${it.unit}"
                binding.addButton.isEnabled = true
            } ?: run {
                binding.selectedFoodCard.visibility = View.GONE
                binding.addButton.isEnabled = false
            }
        }
    }

    private fun setupAddButton() {
        binding.addButton.setOnClickListener {
            viewModel.selectedFood.value?.let { food ->
                viewModel.addFood(food)
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 