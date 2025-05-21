package com.cemilmayuk.kaloritakipapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cemilmayuk.kaloritakipapp.R
import com.cemilmayuk.kaloritakipapp.databinding.FragmentHomeBinding
import com.cemilmayuk.kaloritakipapp.ui.adapters.ConsumedFoodAdapter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var consumedFoodAdapter: ConsumedFoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshDailyGoal()
    }

    private fun setupRecyclerView() {
        consumedFoodAdapter = ConsumedFoodAdapter { consumedFood ->
            viewModel.deleteConsumedFood(consumedFood)
        }
        binding.consumedFoodsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = consumedFoodAdapter
        }
    }

    private fun setupObservers() {
        viewModel.consumedFoods.observe(viewLifecycleOwner) { foods ->
            consumedFoodAdapter.submitList(foods)
            binding.emptyStateText.visibility = if (foods.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.dailyGoal.observe(viewLifecycleOwner) { goal ->
            binding.dailyGoalText.text = "$goal kcal"
            updateProgressBar()
        }

        viewModel.totalCalories.observe(viewLifecycleOwner) { calories ->
            binding.consumedCaloriesText.text = "${calories.toInt()} kcal"
            updateProgressBar()
        }

        viewModel.remainingCalories.observe(viewLifecycleOwner) { remaining ->
            binding.remainingCaloriesText.text = "${remaining.toInt()} kcal"
        }
    }

    private fun updateProgressBar() {
        val goal = viewModel.dailyGoal.value ?: return
        val consumed = viewModel.totalCalories.value ?: 0.0
        val progress = ((consumed / goal) * 100).toInt().coerceIn(0, 100)
        binding.calorieProgressBar.progress = progress
    }

    private fun setupClickListeners() {
        binding.addFoodFab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFoodFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 