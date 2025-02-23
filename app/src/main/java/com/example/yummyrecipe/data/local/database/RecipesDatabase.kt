package com.example.yummyrecipe.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.yummyrecipe.data.local.entities.FavoriteEntity
import com.example.yummyrecipe.data.local.entities.RecipesEntity

@Database(entities = [RecipesEntity::class, FavoriteEntity::class], version = 1, exportSchema = false)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase : RoomDatabase(){

    abstract fun recipesDao():RecipesDao

}