package com.example.dpfoodapp.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.dpfoodapp.databinding.CategoryCardBinding
import com.example.dpfoodapp.model.data.Category

class CategoryAdapterList : RecyclerView.Adapter<CategoryAdapterList.ViewHolderOne>() {
    private lateinit var passpos:((Category,CategoryCardBinding)->Unit)
    inner class ViewHolderOne(val binding: CategoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(data: Category) {

            Glide.with(itemView.context).load(data.strCategoryThumb)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.imgCategory)
            binding.tvCategoryName.text = data.strCategory

            binding.imgCategory.setOnClickListener {
                passpos.invoke(data,binding)
                 //    Log.d("categoryresponses",data.idCategory.toString())
            }
        }

    }

    val differCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.idCategory == newItem.idCategory
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderOne {
        return ViewHolderOne(
            binding = CategoryCardBinding.inflate(
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
    fun categoryToCategoryList(listen:((Category,CategoryCardBinding)->Unit)){
        passpos=listen
    }
}