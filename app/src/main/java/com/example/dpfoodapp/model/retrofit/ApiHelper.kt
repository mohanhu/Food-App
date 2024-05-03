package com.example.dpfoodapp.model.retrofit

class ApiHelper (val apiCall: ApiCall){

    suspend fun getApi() = apiCall.getApi()

    suspend fun getFish(category:String) = apiCall.getFish(category)

    suspend fun getFishDetails(id:String) = apiCall.getFishDetails(id)

    suspend fun getCategories() = apiCall.getCategories()

    suspend fun searchMeals(search:String)=apiCall.searchMeals(search)

}