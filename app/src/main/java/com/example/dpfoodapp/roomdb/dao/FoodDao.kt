package com.example.dpfoodapp.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dpfoodapp.model.data.Meal
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(meal: Meal)

    @Delete
    suspend fun deleteTask(meal: Meal)

    @Query("SELECT * FROM meal_table ")
     fun readAll(): Flow<List<Meal>>

     @Query("SELECT EXISTS(SELECT * from meal_table WHERE idMeal= :exitId)")
     fun checkExist(exitId:String):Boolean
}