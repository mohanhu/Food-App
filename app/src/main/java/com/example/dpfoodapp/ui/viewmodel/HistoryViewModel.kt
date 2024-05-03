package com.example.dpfoodapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.model.modelsearch.SearchTable
import com.example.dpfoodapp.roomdb.database.SearchDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryViewModel(val historyDatabase:SearchDatabase):ViewModel() {

    val searchDetails : Flow<List<SearchTable>> = historyDatabase.searchDao().readAll()

    fun insertTask(search: SearchTable) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDatabase.searchDao().insertTask(search)
        }
    }
    fun deleteTask(search: SearchTable) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDatabase.searchDao().deleteTask(search)
        }
    }
}