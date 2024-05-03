package com.example.dpfoodapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dpfoodapp.databinding.CardlistcardBinding
import com.example.dpfoodapp.model.data.Meal

class SearchAdapter : RecyclerView.Adapter<SearchAdapter.ViewHolderOne>() {
    private lateinit var passpos:((Meal)->Unit)
    inner class ViewHolderOne(val binding: CardlistcardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(data: Meal) {
                Glide.with(itemView.context).load(data.strMealThumb)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgFavMeal)
                binding.tvFavMealName.text = data.strMeal
            binding.imgFavMeal.setOnClickListener {
                passpos.invoke(data)
            }
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
    fun searchDataReceive(listen:((Meal)->Unit)){
        passpos=listen
    }
}