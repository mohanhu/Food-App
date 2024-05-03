package com.example.dpfoodapp.model.retrofit

import com.example.dpfoodapp.model.data.CategorDataClass
import com.example.dpfoodapp.model.data.FoodDataClass
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiCall {

    @POST("random.php")
    suspend fun getApi():Response<FoodDataClass>

    @POST("filter.php")
    suspend fun getFish(@Query ("c") category:String):Response<FoodDataClass>

    @POST("lookup.php")
    suspend fun getFishDetails(@Query ("i") id:String):Response<FoodDataClass>

    @POST("categories.php")
    suspend fun getCategories():Response<CategorDataClass<Any?>>

    @POST("search.php")
    suspend fun searchMeals(@Query("s") search:String):Response<FoodDataClass>
}