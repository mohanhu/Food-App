package com.example.dpfoodapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dpfoodapp.databinding.CardlistcardBinding
import com.example.dpfoodapp.databinding.HistorycardlayBinding
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.model.modelsearch.SearchTable

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.ViewHolderOne>() {
    private lateinit var passpos:((SearchTable)->Unit)
    inner class ViewHolderOne(val binding: HistorycardlayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(data: SearchTable) {
            binding.tvFavMealName.text = data.strMeal

            binding.card.setOnClickListener {
                passpos.invoke(data)
            }
        }
    }
    val differCallback = object : DiffUtil.ItemCallback<SearchTable>() {
        override fun areItemsTheSame(oldItem: SearchTable, newItem: SearchTable): Boolean {
            return oldItem.idMeal == newItem.idMeal
        }

        override fun areContentsTheSame(oldItem: SearchTable, newItem: SearchTable): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOne {
        return ViewHolderOne(
            binding = HistorycardlayBinding.inflate(
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
    fun searchDataReceive(listen:((SearchTable)->Unit)){
        passpos=listen
    }
}