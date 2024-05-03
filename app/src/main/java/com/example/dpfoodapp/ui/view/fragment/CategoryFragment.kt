package com.example.dpfoodapp.ui.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dpfoodapp.R
import com.example.dpfoodapp.databinding.FragmentCategoryBinding
import com.example.dpfoodapp.model.data.Category
import com.example.dpfoodapp.ui.adapter.CategoryAdapterList
import com.example.dpfoodapp.ui.viewmodel.FoodViewModel
import com.example.dpfoodapp.utils.NetworkClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    val foodViewModel: FoodViewModel by activityViewModels<FoodViewModel>()
    private val categoryAdapter: CategoryAdapterList by lazy { CategoryAdapterList() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentCategoryBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        lifecycleScope.launch(Dispatchers.Main) {
            foodViewModel.getCategories()
            foodViewModel.categoryDetailsNetwork.collect(){ catData ->
                when (catData) {
                    is NetworkClass.Loading -> {
                        binding.progressbar.visibility= View.VISIBLE
                        binding.loadingGifMeals.visibility=View.VISIBLE
                        binding.categoryRecycle.visibility=View.GONE
                    }
                    is NetworkClass.Success -> {
                        binding.progressbar.visibility= View.GONE
                        binding.loadingGifMeals.visibility=View.GONE
                        binding.categoryRecycle.visibility=View.VISIBLE
                        categoryAdapter.differ.submitList(catData.item as MutableList<Category>)
                        //categoryAdapter.notifyDataSetChanged()
                        Log.d("categoryResponse",catData.toString())
                    }
                }
            }
        }
        categoryAdapter.categoryToCategoryList {catData,catBinding ->
            catData.strCategory?.let { it1 -> foodViewModel.getFoodFish(it1) }
            findNavController().navigate(R.id.action_categoryFragment2_to_categoryDescList3)
        }
    }

    private fun setAdapter() {
        binding.categoryRecycle.adapter=categoryAdapter
        val gridLayoutManager=  GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
        binding.categoryRecycle.layoutManager=gridLayoutManager
    }
}