package com.example.yummyrecipe.data.remote.api

import com.example.yummyrecipe.model.Recipe
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface RecipeApi {

    @GET("recipes/complexSearch")
    suspend fun getAllRecipes(
        @QueryMap queries : Map<String,String>
    ) : Response<Recipe>

    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @QueryMap searchQuery : Map<String,String>
    ): Response<Recipe>

}