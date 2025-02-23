package com.example.yummyrecipe.data.local.data

import com.example.yummyrecipe.data.local.database.RecipesDao
import com.example.yummyrecipe.data.local.entities.FavoriteEntity
import com.example.yummyrecipe.data.local.entities.RecipesEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private  val recipesDao: RecipesDao
){

    fun readDatabase(): Flow<List<RecipesEntity>> {
        return recipesDao.readRecipes()
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity){
        recipesDao.insertRecipes(recipesEntity)
    }

    fun readFavoriteRecipes(): Flow<List<FavoriteEntity>> {
        return recipesDao.readFavoriteRecipes()
    }

    suspend fun insertRecipes(favoriteEntity: FavoriteEntity){
        recipesDao.insertFavoriteRecipes(favoriteEntity)
    }

    suspend fun deleteFavoriteRecipe(favoriteEntity: FavoriteEntity){
        return recipesDao.deleteFavoriteRecipes(favoriteEntity)
    }

    suspend fun deleteAllFavoriteRecipes(){
        recipesDao.deleteAllFavoriteRecipes()
    }
}