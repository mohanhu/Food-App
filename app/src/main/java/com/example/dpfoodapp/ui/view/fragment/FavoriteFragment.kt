package com.example.dpfoodapp.ui.view.fragment

import android.app.AlertDialog
import android.os.Bundle
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dpfoodapp.R
import com.example.dpfoodapp.databinding.FragmentFavoriteBinding
import com.example.dpfoodapp.roomdb.database.FoodDatabase
import com.example.dpfoodapp.ui.adapter.CategoryList
import com.example.dpfoodapp.ui.viewmodel.FoodViewModel
import com.example.dpfoodapp.ui.viewmodel.RoomDBViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val favouriteAdapter:CategoryList by lazy {CategoryList()}
    private lateinit var favouriteViewmodel:RoomDBViewModel
    private val favViewModel:FoodViewModel by activityViewModels<FoodViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentFavoriteBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()

        val dialogLayout = layoutInflater.inflate(R.layout.cardzoomview, null)
        val builder = AlertDialog.Builder(requireContext(),R.style.customAlert)
        builder.setView(dialogLayout)

        val alert: AlertDialog =builder.create()
        val imgalert = dialogLayout.findViewById<ImageView>(R.id.img_fav_meal_alert)
        val alertMealName=  dialogLayout.findViewById<TextView>(R.id.alert_fav_meal_name)
        favouriteAdapter.receiveImage {datas,b ->
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

        favouriteViewmodel= RoomDBViewModel(FoodDatabase.getInstance(requireContext()))
        lifecycleScope.launch(Dispatchers.Main){
            favouriteViewmodel.mealDetails.collect{
                if(it.isEmpty()){
                    binding.loadingGifMeals.visibility=View.VISIBLE
                    binding.favoriteRecycle.visibility=View.GONE

                }
                else{
                    binding.loadingGifMeals.visibility=View.GONE
                    binding.favoriteRecycle.visibility=View.VISIBLE
                    favouriteAdapter.differ.submitList(it)
                    favouriteAdapter.notifyDataSetChanged()
                }
            }
        }

        favouriteAdapter.receiveData {datacat,catbinding ->
            val bundle = bundleOf("catmeallist"  to  datacat.idMeal)
            findNavController().navigate(R.id.action_favoriteFragment_to_mealInstructionFragment3,bundle,null,null)
        }

        //swipe delete
        val itemTouchHelper= object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position= viewHolder.adapterPosition
                val recentStrore = favouriteAdapter.differ.currentList[position]
                favouriteViewmodel.deleteTask(recentStrore)
                Snackbar.make(binding.favoriteRecycle,"Deleted "+"${favouriteAdapter.differ.currentList[position].strCategory}",Snackbar.LENGTH_SHORT)
                    .setAction("Undo",View.OnClickListener {
                        favouriteViewmodel.insertTask(recentStrore)
                    }).show()
            }
        }
        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(binding.favoriteRecycle)
    }

    private fun setAdapter() {
        binding.favoriteRecycle.adapter=favouriteAdapter
        val gridLayoutManager= GridLayoutManager(requireContext(),2,GridLayoutManager.VERTICAL,false)
        binding.favoriteRecycle.layoutManager=gridLayoutManager
    }

}