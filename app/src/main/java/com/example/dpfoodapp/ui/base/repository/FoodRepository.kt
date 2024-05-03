package com.example.dpfoodapp.ui.base.repository

import com.example.dpfoodapp.model.retrofit.ApiCall
import com.example.dpfoodapp.model.retrofit.ApiHelper

class FoodRepository(private val apiHelper: ApiHelper) {

    suspend fun getApi() = apiHelper.getApi()

    suspend fun getFish(category:String) = apiHelper.getFish(category)

    suspend fun getFishDetails(id:String) = apiHelper.getFishDetails(id)

    suspend fun getCategories() = apiHelper.getCategories()

    suspend fun searchMeals(search:String)=apiHelper.searchMeals(search)
}