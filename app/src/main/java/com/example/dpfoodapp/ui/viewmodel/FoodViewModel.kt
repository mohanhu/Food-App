package com.example.dpfoodapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dpfoodapp.model.data.Category
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.ui.base.repository.FoodRepository
import com.example.dpfoodapp.model.retrofit.ApiConnection
import com.example.dpfoodapp.model.retrofit.ApiHelper
import com.example.dpfoodapp.utils.NetworkClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class FoodViewModel : ViewModel() {

    val networkClass = MutableStateFlow<NetworkClass<Any?>>(NetworkClass.Empty)
    val fishNetworkClass = MutableStateFlow<NetworkClass<Any?>>(NetworkClass.Empty)
    val mealDetails = MutableStateFlow<NetworkClass<Any?>>(NetworkClass.Empty)
    val categoryDetailsNetwork = MutableStateFlow<NetworkClass<Any?>>(NetworkClass.Empty)
    val searchResponseLive = MutableStateFlow<NetworkClass<Any?>>(NetworkClass.Empty)
    val passHomeToInst =  MutableStateFlow<List<Meal>?>(getMealDetail())

    private fun getMealDetail(): List<Meal>? {
        val meal= Meal(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ",",
            "",
            "",
            "",
            ",",
            ",",
            ",",
            "",
            "",
            "",
            "",
            ",",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )
        return listOf(meal)
    }

    val apicall= ApiConnection.retrofit
    val repository:FoodRepository = FoodRepository(ApiHelper(apicall))
    init {
       getFood()
    }

    fun getFood() {
        viewModelScope. launch(Dispatchers.IO) {
            networkClass.value=(NetworkClass.Loading)
            val response = try {
                repository.getApi()
            } catch (e: Exception) {
                networkClass.value=(NetworkClass.Error(e.toString()))
                return@launch
            }
            if (response.isSuccessful || response.body() != null) {
                val submitVal: List<Meal>?
                response.body()?.meals.let { submitVal = it }
                networkClass.value=NetworkClass.Success(submitVal)
            } else {
                networkClass.value=(NetworkClass.Error("Error"))
            }
        }
    }

    fun getFoodFish(category:String) {
        viewModelScope.launch(Dispatchers.IO) {
            fishNetworkClass.value=(NetworkClass.Loading)
            val responseFish = try {
                repository.getFish(category)
            } catch (e: Exception) {
                fishNetworkClass.value=(NetworkClass.Error(e.toString()))
                return@launch
            }
            if (responseFish.isSuccessful || responseFish.body() != null) {
                val submitVals: List<Meal>

                responseFish.body()?.meals.let {
                    if (it != null) {
                        submitVals = it
                        fishNetworkClass.value=NetworkClass.Success(submitVals)
                    }
                }

            } else {
                fishNetworkClass.value=(NetworkClass.Error("Error"))
            }
        }
    }

    fun getFoodFishId(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mealDetails.value=(NetworkClass.Loading)
            val responseFishDetails = try {
                repository.getFishDetails(id)
            } catch (e: Exception) {
                mealDetails.value=(NetworkClass.Error(e.toString()))
                return@launch
            }
            if (responseFishDetails.isSuccessful || responseFishDetails.body() != null) {
                val submitVals: List<Meal>

                responseFishDetails.body()?.meals.let {
                    if (it != null) {
                        submitVals = it
                        mealDetails.value=(submitVals.let { NetworkClass.Success(submitVals) })
                    }
                }

            } else {
                mealDetails.value=(NetworkClass.Error("Error"))
            }
        }
    }

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            categoryDetailsNetwork.value=(NetworkClass.Loading)
            val responseCategory = try {
                repository.getCategories()
            } catch (e: Exception) {
                categoryDetailsNetwork.value=(NetworkClass.Error(e.toString()))
                return@launch
            }
            if (responseCategory.isSuccessful || responseCategory.body() != null) {
                val cateVals: List<Category>
                responseCategory.body()?.categories.let {
                    if (it != null) {
                        cateVals=it
                        categoryDetailsNetwork.value=(NetworkClass.Success(cateVals) )
                    }
                }
            } else {
                categoryDetailsNetwork.value=(NetworkClass.Error("Error"))
            }
        }
    }

    fun searchMeals(search:String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchResponseLive.value=(NetworkClass.Loading)
            val responseSearch = try {
                repository.searchMeals(search)
            } catch (e: Exception) {
                searchResponseLive.value=(NetworkClass.Error(e.toString()))
                return@launch
            }
            if (responseSearch.isSuccessful || responseSearch.body() != null) {
                val cateVals: List<Meal>
                responseSearch.body()?.meals.let {
                    if (it != null) {
                        cateVals=it
                        searchResponseLive.value=(NetworkClass.Success(cateVals) )
                    }
                }
            } else {
                searchResponseLive.value=(NetworkClass.Error("Error"))
            }
        }
    }

}