package com.example.dpfoodapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.roomdb.database.FoodDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber

class RoomDBViewModel(private val foodRoomDatabase: FoodDatabase) : ViewModel() {

    val mealDetails : Flow<List<Meal>> = foodRoomDatabase.foodDao().readAll()
    var bool:Boolean=false
    fun insertTask(meal: Meal) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRoomDatabase.foodDao().insertTask(meal)
        }
    }
    fun deleteTask(meal: Meal) {
        viewModelScope.launch(Dispatchers.IO) {
            foodRoomDatabase.foodDao().deleteTask(meal)
        }
    }

    fun checkExist(checkId:String):Boolean {
        viewModelScope.launch(Dispatchers.IO) {
            bool = foodRoomDatabase.foodDao().checkExist(checkId)
        }
        Timber.tag("floatrsponse").d(bool.toString())
        return bool
    }
}