package com.example.dpfoodapp.ui.view.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dpfoodapp.R
import com.example.dpfoodapp.databinding.FragmentMealInstructionBinding
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.roomdb.database.FoodDatabase

import com.example.dpfoodapp.ui.viewmodel.FoodViewModel
import com.example.dpfoodapp.ui.viewmodel.RoomDBViewModel
import com.example.dpfoodapp.utils.NetworkClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MealInstructionFragment : Fragment() {
    private lateinit var binding: FragmentMealInstructionBinding
    private val foodViewModel: FoodViewModel by activityViewModels<FoodViewModel>()
    private lateinit var foodRoomViewModel: RoomDBViewModel
    private lateinit var meal: Meal

    var check = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMealInstructionBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        foodRoomViewModel= RoomDBViewModel(FoodDatabase.getInstance(requireContext()))
        insertTaskDb()
        val animation =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = null
        conditionCheckId()
        observeModelValue()
    }

    private fun conditionCheckId() {
        val args = arguments
        val categoryId = args?.getString("catmeallist").toString()
        Log.d("categoryresponses", categoryId.toString())

        if (categoryId.toString() == "null") {
            fetchIdDetails()
            observeModelValue()
        } else {
            getMealsDetailsByid(categoryId)
            observeModelValue()

        }
    }

    private fun getMealsDetailsByid(categoryId: String) {
        foodViewModel.getFoodFishId(categoryId)
        lifecycleScope.launchWhenCreated {
            foodViewModel.mealDetails.collect() {
                when (it) {
                    is NetworkClass.Loading -> {
                        binding.progressbar.visibility = View.VISIBLE
                        binding.loadingMeals.visibility = View.VISIBLE
                        binding.mealImage.visibility = View.GONE
                    }
                    is NetworkClass.Success -> {
                        binding.progressbar.visibility = View.GONE
                        binding.mealImage.visibility = View.VISIBLE
                        binding.loadingMeals.visibility = View.GONE
                        (it.item as List<Meal>?)?.let { it1 ->
                            foodViewModel.passHomeToInst.value = it1
                        }
                    }
                    else -> {
                        binding.progressbar.visibility = View.VISIBLE
                        binding.loadingMeals.visibility = View.VISIBLE
                        binding.mealImage.visibility = View.GONE
                    }
                }
            }
        }
        /*observeModelValue()*/
    }

    private fun insertTaskDb() {

        binding.floatingActionButton.setOnClickListener {
            if (!check) {
                Log.d("buttonClick", "2")
                foodRoomViewModel.insertTask(meal)
                check = true
                binding.floatingActionButton.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red))
            } else {
                foodRoomViewModel.deleteTask(meal)
                check = false
                Log.d("buttonClick", "1")
                binding.floatingActionButton.backgroundTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.blue))

            }
        }
    }

    private fun fetchIdDetails() {
        lifecycleScope.launchWhenCreated {
            foodViewModel.passHomeToInst.collect() { data ->
                data?.get(0).let { data ->
                    if (data?.strYoutube == null) {
                        data?.idMeal.let {
                            data?.idMeal?.let { it1 ->
                                foodViewModel.getFoodFishId(
                                    it1
                                )
                            }
                        }
                        foodViewModel.mealDetails.collect() {
                            when (it) {
                                is NetworkClass.Loading -> {
                                    binding.progressbar.visibility = View.VISIBLE
                                    binding.loadingMeals.visibility = View.VISIBLE
                                    binding.mealImage.visibility = View.GONE
                                }
                                is NetworkClass.Success -> {
                                    binding.progressbar.visibility = View.GONE
                                    binding.mealImage.visibility = View.VISIBLE
                                    binding.loadingMeals.visibility = View.GONE
                                    (it.item as List<Meal>?)?.let { it1 ->
                                        foodViewModel.passHomeToInst.value = it1
                                    }
                                }
                                else -> {
                                    binding.progressbar.visibility = View.VISIBLE
                                    binding.loadingMeals.visibility = View.VISIBLE
                                }
                            }
                        }

                    }/* else {
                        observeModelValue()
                    }*/
                }
            }
        }
    }

    private fun observeModelValue() {
        lifecycleScope.launchWhenCreated {
            foodViewModel.passHomeToInst.collect() { data ->
                data?.get(0).let { data ->
                    if (foodRoomViewModel.checkExist(data?.idMeal.toString())) {
                        check = true
                        binding.floatingActionButton.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.red
                            )
                        )
                    } else {
                        check = false
                        binding.floatingActionButton.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.blue
                            )
                        )
                    }
                    if (data != null) {
                        meal = data
                    }
                    binding.mealImage.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(data?.strMealThumb)
                        .apply(RequestOptions.centerCropTransform())
                        .into(binding.mealImage)
                    binding.categoryName.text = data?.strCategory
                    binding.location.text = data?.strArea
                    binding.description.text = data?.strInstructions
                    binding.youtube.setOnClickListener {
                        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse(data?.strYoutube))
                        val intentBrowser =
                            Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="))
                        try {
                            startActivity(intentApp)
                        } catch (ex: ActivityNotFoundException) {
                            startActivity(intentBrowser)
                        }
                    }

                }
            }
        }

    }

}
