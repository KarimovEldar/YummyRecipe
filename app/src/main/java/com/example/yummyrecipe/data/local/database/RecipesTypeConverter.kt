package com.example.yummyrecipe.data.local.database

import androidx.room.TypeConverter
import com.example.yummyrecipe.model.Recipe
import com.google.gson.Gson
import com.example.yummyrecipe.model.Result
import com.google.gson.reflect.TypeToken

class RecipesTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun foodRecipesToString(recipes: Recipe):String{
        return gson.toJson(recipes)
    }

    @TypeConverter
    fun stringToFoodRecipes(data:String):Recipe{
        val listType = object : TypeToken<Recipe>(){}.type
        return gson.fromJson(data,listType)
    }

    @TypeConverter
    fun resultToString(result: Result):String{
        return gson.toJson(result)
    }

    @TypeConverter
    fun stringToResult(data:String):Result{
        val listType = object : TypeToken<Result>(){}.type
        return gson.fromJson(data,listType)
    }

}