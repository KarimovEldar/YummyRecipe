package com.example.yummyrecipe.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.yummyrecipe.data.local.entities.FavoriteEntity
import com.example.yummyrecipe.data.local.entities.RecipesEntity
import com.example.yummyrecipe.data.remote.repositories.Repository
import com.example.yummyrecipe.model.Recipe
import com.example.yummyrecipe.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {


    /** ROOM DATABASE **/

    val readDatabase : LiveData<List<RecipesEntity>> = repository.local.readDatabase().asLiveData()
    val readFavoriteEntity : LiveData<List<FavoriteEntity>> = repository.local.readFavoriteRecipes().asLiveData()

    fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch (Dispatchers.IO){
            repository.local.insertRecipes(recipesEntity)
        }

    fun insertFavoriteRecipes(favoriteEntity: FavoriteEntity) =
        viewModelScope.launch (Dispatchers.IO){
            repository.local.insertRecipes(favoriteEntity)
        }

    fun deleteFavoriteRecipe(favoriteEntity: FavoriteEntity) =
        viewModelScope.launch (Dispatchers.IO){
            repository.local.deleteFavoriteRecipe(favoriteEntity)
        }

    fun deleteAllFavoriteRecipes() =
        viewModelScope.launch (Dispatchers.IO){
            repository.local.deleteAllFavoriteRecipes()
        }

    /** RETROFIT DATABASE **/
    var recipesResponse : MutableLiveData<NetworkResult<Recipe>> = MutableLiveData()
    var searchRecipesResponse : MutableLiveData<NetworkResult<Recipe>> = MutableLiveData()

    fun getAllRecipes(queries : Map<String,String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    fun searchRecipes(searchQuery : Map<String,String>)= viewModelScope.launch {
        searchRecipesSafeCall(searchQuery)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String,String>) {
        recipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try {
                val response = repository.remote.getAllRecipes(queries)
                recipesResponse.value = handleRecipesResponse(response)

                val recipe = recipesResponse.value!!.data
                if (recipe != null){
                    offlineCacheRecipes(recipe)
                }
            }catch (e:Exception){
                recipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        }else{
            recipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }
    }

    private suspend fun searchRecipesSafeCall(searchQuery: Map<String, String>) {
        searchRecipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try {
                val response = repository.remote.searchRecipes(searchQuery)
                searchRecipesResponse.value = handleRecipesResponse(response)

                val recipe = searchRecipesResponse.value!!.data
                if (recipe != null){
                    offlineCacheRecipes(recipe)
                }
            }catch (e:Exception){
                searchRecipesResponse.value = NetworkResult.Error("Recipes not found.")
            }
        }else{
            searchRecipesResponse.value = NetworkResult.Error("No Internet Connection.")
        }    }

    private fun offlineCacheRecipes(recipe: Recipe) {
        val recipesEntity = RecipesEntity(recipe)
        insertRecipes(recipesEntity)
    }

    private fun handleRecipesResponse(response: Response<Recipe>): NetworkResult<Recipe>? {
        when {
            response.message().toString().contains("timeout") ->{
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 ->{
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.results.isNullOrEmpty() ->{
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val recipe = response.body()
                return NetworkResult.Success(recipe)
            }
            else ->{
                return NetworkResult.Error(response.message())
            }

        }
    }

    private fun hasInternetConnection():Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activityNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activityNetwork) ?: return true
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET ) -> true
            else -> false
        }
    }

}