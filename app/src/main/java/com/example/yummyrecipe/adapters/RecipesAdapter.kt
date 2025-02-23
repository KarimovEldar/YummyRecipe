package com.example.yummyrecipe.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yummyrecipe.databinding.RecipesRowLayoutBinding
import com.example.yummyrecipe.model.Recipe
import com.example.yummyrecipe.model.Result
import com.example.yummyrecipe.util.RecipesDiffUtil

class RecipesAdapter: RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {

    private var recipes = emptyList<Result>()

    class RecipesViewHolder(private val binding : RecipesRowLayoutBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(result : Result){
            binding.result = result
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup):RecipesViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent,false)
                return RecipesViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        return RecipesViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)
    }

    fun setData(newList : Recipe){
        val recipesDiffUtil = RecipesDiffUtil(recipes,newList.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newList.results
        diffUtilResult.dispatchUpdatesTo(this)
    }

}