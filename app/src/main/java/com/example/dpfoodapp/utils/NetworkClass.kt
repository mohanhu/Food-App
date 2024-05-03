package com.example.dpfoodapp.utils

import com.example.dpfoodapp.model.data.FoodDataClass
import com.example.dpfoodapp.model.data.Meal

sealed class NetworkClass<out T>() {
    object Loading : NetworkClass<Nothing>()
    data class Error<T>(val throwable: String) : NetworkClass<T>()
    data class Success<T>(val item: List<T>?) : NetworkClass<T>()
    object Empty:NetworkClass<Nothing>()
}
