package com.example.dpfoodapp.roomdb.database

import android.content.Context
import androidx.room.*
import com.example.dpfoodapp.model.modelsearch.SearchTable
import com.example.dpfoodapp.roomdb.FoodTypeConverter
import com.example.dpfoodapp.roomdb.dao.FoodDao
import com.example.dpfoodapp.roomdb.dao.SearchDao
@Database(entities = [SearchTable::class], version = 1, exportSchema = false)
@TypeConverters(FoodTypeConverter::class)
abstract class SearchDatabase:RoomDatabase() {

    abstract fun searchDao():SearchDao

    companion object{
        @Volatile
        var INSTANCE:SearchDatabase?=null

        @Synchronized()
        fun getInstanceSearch(context: Context):SearchDatabase{
            if(INSTANCE==null){
                INSTANCE= Room.databaseBuilder(context,SearchDatabase::class.java,"history_table").build()
            }
            return INSTANCE as SearchDatabase
        }
    }
}