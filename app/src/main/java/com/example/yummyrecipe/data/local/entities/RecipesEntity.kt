package com.example.yummyrecipe.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yummyrecipe.model.Recipe
import com.example.yummyrecipe.util.Constants.Companion.TABLE_NAME

@Entity(tableName = TABLE_NAME)
class RecipesEntity (
    var recipe: Recipe
){
    @PrimaryKey(autoGenerate = false)
    var id = 0
}