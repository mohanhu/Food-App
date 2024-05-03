package com.example.dpfoodapp.roomdb.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dpfoodapp.model.data.Meal
import com.example.dpfoodapp.roomdb.FoodTypeConverter
import com.example.dpfoodapp.roomdb.dao.FoodDao


@Database(entities = [Meal::class], version = 1, exportSchema = false)
@TypeConverters(FoodTypeConverter::class)
abstract class FoodDatabase :RoomDatabase() {

    abstract fun foodDao(): FoodDao
    companion object {
        @Volatile
        var INSTANCE: FoodDatabase? = null

        @Synchronized()
        fun getInstance(context: Context): FoodDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, FoodDatabase::class.java, "meal_table")
                    .fallbackToDestructiveMigration().build()
            }
            return INSTANCE as FoodDatabase
        }
    }
}