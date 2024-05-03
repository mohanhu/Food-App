package com.example.dpfoodapp.ui.view.fragment

import android.content.Intent
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import com.example.dpfoodapp.databinding.FragmentHomeBinding
import com.example.dpfoodapp.model.data.Category
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.ui.adapter.CategoryAdapterList
import com.example.dpfoodapp.ui.adapter.FoodAdapter
import com.example.dpfoodapp.ui.view.main.SearchActivity
import com.example.dpfoodapp.ui.viewmodel.FoodViewModel
import com.example.dpfoodapp.utils.NetworkClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    val foodViewModel: FoodViewModel by activityViewModels<FoodViewModel>()
    private val fishAdapter: FoodAdapter by lazy { FoodAdapter() }
    private val categoryAdapter:CategoryAdapterList by lazy { CategoryAdapterList() }
    lateinit var randomeal: List<Meal>
    private val popularList:ArrayList<Meal> by lazy { ArrayList<Meal>() }
    private lateinit var popularlayoutManager:LinearLayoutManager
    var show=false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
        foodMenuUpdate()
        callbackFuctions()
        pagination()
        val animation= TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition=animation


        val activityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if (result.resultCode== AppCompatActivity.RESULT_OK){
                val intent =result.data
                val listpass:List<Meal> = listOf(intent?.getSerializableExtra("message")) as List<Meal>
                foodViewModel.passHomeToInst.value=listpass
              //  Toast.makeText(requireContext(), intent?.getSerializableExtra("message").toString(), Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_homeFragment2_to_mealInstructionFragment2)
            }
        }

        binding.imgSearch.setOnClickListener {
            Intent(requireContext(),SearchActivity::class.java)
                .apply {
                    activityResult.launch(this)
                }
        }
    }

    private fun pagination() {
        var i=0
        val listpopular: List<String> = listOf("Beef", "Chicken", "Dessert", "Lamb")
        binding.recViewMealsPopular.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(i == listpopular.size-1) i=0
                val childcount = popularlayoutManager.childCount
                val visiblecount = (
                        popularlayoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                val total = fishAdapter.itemCount

                if (!show) {
                    if ((childcount + visiblecount) - 1 == total) {
                        show = true
                        foodViewModel.getFoodFish(listpopular[i])
                        Log.d("pagination", listpopular[i])
                        i++
                    }
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }
    private fun callbackFuctions() {
        fishAdapter.passData {data,popbinding ->
            foodViewModel.passHomeToInst.value=data

            findNavController().navigate(R.id.action_homeFragment2_to_mealInstructionFragment2,null,null,null)
        }
        categoryAdapter.categoryToCategoryList {catData,catBinding ->
            catData.strCategory?.let { it1 -> foodViewModel.getFoodFish(it1) }
            findNavController().navigate(R.id.action_homeFragment2_to_categoryDescList2)
        }
    }

    private fun setAdapter() {
        binding.recViewMealsPopular.adapter = fishAdapter
        binding.recyclerViewCategory.adapter=categoryAdapter
        popularlayoutManager=LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recViewMealsPopular.layoutManager = popularlayoutManager
        val catlayoutManager = GridLayoutManager(requireContext(),3,GridLayoutManager.VERTICAL,false)
        binding.recyclerViewCategory.layoutManager=catlayoutManager
    }

    private fun foodMenuUpdate() {
        lifecycleScope.launch(Dispatchers.Main) {
            foodViewModel.getFood()
            foodViewModel.networkClass.collect() { data ->
                when (data) {
                    is NetworkClass.Loading -> {
                        //  binding.loadingHome.visibility = View.VISIBLE
                        binding.progressbar.visibility = View.VISIBLE

                    }
                    is NetworkClass.Success -> {
                        randomeal = ((data.item as List<Meal>?)!!)
                        binding.loadingHome.visibility = View.GONE
                        binding.progressbar.visibility = View.GONE
                        binding.imgRandomMeal.visibility = View.VISIBLE
                        Glide.with(requireActivity()).load(data.item?.get(0)?.strMealThumb)
                            .apply(RequestOptions.fitCenterTransform())
                            .into(binding.imgRandomMeal)
                    }
                    else -> {
                        binding.loadingHome.visibility = View.VISIBLE
                        binding.progressbar.visibility = View.VISIBLE
                    }
                }

                binding.imgRandomMeal.setOnClickListener {
                    try {
                        // foodViewModel.setPassHomeToInst(randomeal)
                        foodViewModel.passHomeToInst.value = randomeal
                        val extras = FragmentNavigatorExtras(binding.imgRandomMeal to "image_big")
                        findNavController().navigate(
                            R.id.action_homeFragment2_to_mealInstructionFragment2,
                            null,
                            null,
                            extras
                        )
                    } catch (e: UninitializedPropertyAccessException) {
                    }

                }
            }
        }
        lifecycleScope.launch(Dispatchers.Main) {
            foodViewModel.getFoodFish("SeaFood")
            foodViewModel.fishNetworkClass.collect() { fishdata ->
                when (fishdata) {
                    is NetworkClass.Loading -> {
                        binding.progressbar.visibility = View.VISIBLE
                    }
                    is NetworkClass.Success -> {
                        binding.progressbar.visibility = View.GONE
                        binding.loadingHome.visibility = View.GONE
                        binding.recViewMealsPopular.visibility = View.VISIBLE
                        popularList.addAll(fishdata.item as MutableList<Meal>)
                        fishAdapter.differ.submitList(popularList)
                        //fishAdapter.notifyDataSetChanged()
                        show = false
                    }
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            foodViewModel.getCategories()
            foodViewModel.categoryDetailsNetwork.collect(){ catData ->
                when (catData) {
                    is NetworkClass.Loading -> {
                        binding.progressbar.visibility= View.VISIBLE
                        binding.recyclerViewCategory.visibility=View.GONE
                    }
                    is NetworkClass.Success -> {
                        binding.progressbar.visibility= View.GONE
                        binding.loadingHome.visibility=View.GONE

                        binding.recyclerViewCategory.visibility=View.VISIBLE
                        categoryAdapter.differ.submitList(catData.item as MutableList<Category>)
                        //categoryAdapter.notifyDataSetChanged()
                        Log.d("categoryResponse",catData.toString())
                    }
                    is NetworkClass.Error ->{
                        binding.progressbar.visibility= View.VISIBLE
                        Log.d("categoryResponse","error")
                    }
                }

            }
        }
    }
}