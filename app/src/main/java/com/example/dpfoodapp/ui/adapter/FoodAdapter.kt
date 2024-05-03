package com.example.dpfoodapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dpfoodapp.databinding.FragmentMealInstructionBinding
import com.example.dpfoodapp.databinding.PopularItemCardBinding
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.ui.viewmodel.FoodViewModel

class FoodAdapter:RecyclerView.Adapter<FoodAdapter.ViewHolderOne>() {
    private lateinit var passpos:((List<Meal>,PopularItemCardBinding)->Unit)
    inner class ViewHolderOne(val binding: PopularItemCardBinding):RecyclerView.ViewHolder(binding.root){

        fun bindItem(data:Meal){

            Glide.with(itemView.context).load(data.strMealThumb)
                .apply(RequestOptions.fitCenterTransform())
                .into(binding.imgFish)
            binding.imgFish.setOnClickListener {

                Log.d("messageFish","${data.idMeal}")
               passpos.invoke(listOf(data),binding)
            }
        }

    }
    val differCallback = object :DiffUtil.ItemCallback<Meal>(){
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.idMeal == newItem.idMeal
        }

        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem==newItem
        }

    }
    val differ = AsyncListDiffer(this,differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOne {
        return ViewHolderOne(binding = PopularItemCardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolderOne, position: Int) {
        holder.bindItem(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    fun passData(listen:((List<Meal>,PopularItemCardBinding)->Unit)){
        passpos=listen
    }
}