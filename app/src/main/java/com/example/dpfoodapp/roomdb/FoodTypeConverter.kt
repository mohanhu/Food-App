package com.example.dpfoodapp.roomdb

import androidx.room.TypeConverter
import androidx.room.TypeConverters


@TypeConverters
class FoodTypeConverter {
    @TypeConverter
    fun anyToString(attr:Any?):String{
        if(attr==null){
            return ""
        }
        return attr as String
    }
    @TypeConverter
    fun stringToAny(attr: String?):Any{
        if (attr==null){
            return ""
        }
        return attr
    }

}