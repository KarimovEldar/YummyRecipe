package com.example.yummyrecipe.data.remote.data

import com.example.yummyrecipe.data.remote.api.RecipeApi
import com.example.yummyrecipe.model.Recipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val recipesApi: RecipeApi
) {

    suspend fun getAllRecipes(queries : Map<String,String>): Response<Recipe> {
        return recipesApi.getAllRecipes(queries)
    }

    suspend fun searchRecipes(searchQuery : Map<String,String>): Response<Recipe> {
        return recipesApi.searchRecipes(searchQuery)
    }

}