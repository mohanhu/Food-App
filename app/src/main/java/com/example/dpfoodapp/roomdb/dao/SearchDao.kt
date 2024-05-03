package com.example.dpfoodapp.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.model.modelsearch.SearchTable
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(search:SearchTable)

    @Delete
    suspend fun deleteTask(search: SearchTable)

    @Query("SELECT * FROM history_table ORDER BY idMeal DESC")
    fun readAll(): Flow<List<SearchTable>>

}