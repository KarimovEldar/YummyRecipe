package com.example.yummyrecipe.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.example.yummyrecipe.data.local.entities.RecipesEntity
import com.example.yummyrecipe.model.Recipe
import com.example.yummyrecipe.util.NetworkResult

class RecipesBinding {
    companion object{

        @BindingAdapter("readApiResponse","readDatabase", requireAll = true)
        @JvmStatic
        fun handleReadDatabaseErrors(
            view: View,
            apiResponse: NetworkResult<Recipe>?,
            database: List<RecipesEntity>?
        ){
            when(view){
                is ImageView ->{
                    view.isVisible = apiResponse is NetworkResult.Error && database.isNullOrEmpty()
                }
                is TextView -> {
                    view.isVisible = apiResponse is NetworkResult.Error && database.isNullOrEmpty()
                    view.text = apiResponse?.message.toString()
                }
            }
        }
    }
}