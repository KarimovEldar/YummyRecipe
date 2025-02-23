package com.example.yummyrecipe.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yummyrecipe.util.Constants.Companion.FAVORITE_TABLE_NAME
import com.example.yummyrecipe.model.Result

@Entity(tableName = FAVORITE_TABLE_NAME)
class FavoriteEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int ,
    var result: Result
)