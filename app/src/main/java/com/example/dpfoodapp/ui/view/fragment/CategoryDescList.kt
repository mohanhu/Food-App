package com.example.dpfoodapp.ui.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dpfoodapp.R
import com.example.dpfoodapp.databinding.FragmentCategoryDescListBinding
import com.example.dpfoodapp.model.data.FoodDataClass
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.ui.adapter.CategoryAdapterList
import com.example.dpfoodapp.ui.adapter.CategoryList
import com.example.dpfoodapp.ui.viewmodel.FoodViewModel
import com.example.dpfoodapp.utils.NetworkClass

class CategoryDescList : Fragment() {
    private lateinit var binding: FragmentCategoryDescListBinding
    private val categoryListAdapter: CategoryList by lazy { CategoryList() }
    private val foodViewModel by activityViewModels<FoodViewModel>()
    private lateinit var listLayoutManager:GridLayoutManager
    var show=false
    private val categoryList:ArrayList<Meal> by lazy { ArrayList<Meal>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCategoryDescListBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        observeDataNetwrk()
        val dialogLayout = layoutInflater.inflate(R.layout.cardzoomview, null)
        val builder = AlertDialog.Builder(requireContext(),R.style.customAlert)
        builder.setView(dialogLayout)

       val alert:AlertDialog =builder.create()
        val imgalert = dialogLayout.findViewById<ImageView>(R.id.img_fav_meal_alert)
        val alertMealName=  dialogLayout.findViewById<TextView>(R.id.alert_fav_meal_name)
        categoryListAdapter.receiveImage {datas,b ->
            if(b){
                val animZoomIn = AnimationUtils.loadAnimation(requireContext(),
                    R.anim.zoomin)
                dialogLayout.startAnimation(animZoomIn)
                Glide.with(requireContext()).load(datas.strMealThumb)
                    .apply(RequestOptions.fitCenterTransform())
                    .into(imgalert)
                alert.show()
                alertMealName.text = datas.strMeal
            }
            else {
                alert.dismiss()
            }

        }
        categoryListAdapter.receiveData {datalist,catlistbinding ->
            val bundle = bundleOf("catmeallist"  to  datalist.idMeal)
            findNavController().navigate(R.id.action_categoryDescList2_to_mealInstructionFragment2,bundle,null,null)
        }
    }

    private fun observeDataNetwrk() {
        lifecycleScope.launchWhenCreated {
            foodViewModel.fishNetworkClass.collect() { fishdata ->
                when (fishdata) {
                    is NetworkClass.Loading -> {
                        binding.loadingGifMeals.visibility = View.VISIBLE
                        binding.mealRecyclerview.visibility = View.GONE
                    }
                    is NetworkClass.Success -> {
                        binding.loadingGifMeals.visibility = View.GONE
                        binding.mealRecyclerview.visibility = View.VISIBLE
                        categoryList.addAll(fishdata.item as MutableList<Meal>)
                        categoryListAdapter.differ.submitList(categoryList)
                        categoryListAdapter.notifyDataSetChanged()
                        show = false
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        binding.mealRecyclerview.adapter=categoryListAdapter
        listLayoutManager = GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
        binding.mealRecyclerview.layoutManager=listLayoutManager

    }

}