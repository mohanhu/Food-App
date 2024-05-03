package com.example.dpfoodapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dpfoodapp.R
import com.example.dpfoodapp.databinding.CardlistcardBinding
import com.example.dpfoodapp.databinding.CategoryCardBinding
import com.example.dpfoodapp.model.data.Meal

class CategoryList : RecyclerView.Adapter<CategoryList.ViewHolderOne>() {
    private lateinit var passpos:((Meal,CardlistcardBinding)->Unit)
    private lateinit var passButton:((Meal,Boolean)->Unit)
    inner class ViewHolderOne(val binding:CardlistcardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(data: Meal) {
            Glide.with(itemView.context).load(data.strMealThumb)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgFavMeal)
            binding.tvFavMealName.text=data.strMeal
            binding.card.setOnClickListener {
                try{
                    passpos.invoke(data,binding)
                }
                catch (e:Exception){}
            }
            binding.card.setOnLongClickListener{
                it.setOnTouchListener(View.OnTouchListener { v, event ->
                    when (event.action) {
                         MotionEvent.AXIS_PRESSURE -> {
                             try{
                                 passButton.invoke(data, true)
                             }
                             catch (e:Exception){}
                             Log.d("categoryresponses","touch")
                          return@OnTouchListener true
                        }
                        else -> {
                            passButton.invoke(data, false)
                            Log.d("categoryresponses","cancel")
                            return@OnTouchListener false
                        }
                    }
                })
                true
            }
           // Log.d("categoryresponses",data.idMeal)
        }
    }

    val differCallback = object : DiffUtil.ItemCallback<Meal>() {
        override fun areItemsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem.idMeal == newItem.idMeal
        }
        override fun areContentsTheSame(oldItem: Meal, newItem: Meal): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOne {
        return ViewHolderOne(
            binding = CardlistcardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolderOne, position: Int) {
        holder.bindItem(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun receiveData(listen:((Meal,CardlistcardBinding)->Unit)){
        passpos=listen
    }

    fun receiveImage(listener:((Meal,Boolean)->Unit)){
        passButton=listener
    }
}