package com.example.yummyrecipe.adapters

import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yummyrecipe.R
import com.example.yummyrecipe.data.local.entities.FavoriteEntity
import com.example.yummyrecipe.databinding.FavoriteRowLayoutBinding
import com.example.yummyrecipe.ui.fragments.favorite.FavoriteFragmentDirections
import com.example.yummyrecipe.util.RecipesDiffUtil
import com.example.yummyrecipe.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar

class FavoritesAdapter (
    private val requireActivity: FragmentActivity,
    private val mainViewModel: MainViewModel
):RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>(), ActionMode.Callback {

    private var multiSelection = false
    private var selectedRecipes = arrayListOf<FavoriteEntity>()

    private lateinit var rootView: View
    private lateinit var mActionMode: ActionMode
    private var myViewHolder = arrayListOf<FavoriteViewHolder>()

    private var favoriteRecipes = emptyList<FavoriteEntity>()

    class FavoriteViewHolder(val binding : FavoriteRowLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(favoritesEntity: FavoriteEntity) {
            binding.favoritesEntity = favoritesEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): FavoriteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRowLayoutBinding.inflate(layoutInflater, parent, false)
                return FavoriteViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        myViewHolder.add(holder)
        rootView = holder.itemView.rootView

        val currentRecipe = favoriteRecipes[position]
        holder.bind(currentRecipe)

        saveItemStateOnScroll(currentRecipe,holder)

        //Single Click Listener
        holder.binding.favoriteRowLayout.setOnClickListener {
            if (multiSelection){
                applySelection(holder,currentRecipe)
            }else{
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToDetailsActivity(currentRecipe.result)
                holder.itemView.findNavController().navigate(action)
            }
        }

        //Long Click Listener
        holder.binding.favoriteRowLayout.setOnLongClickListener {
            if(!multiSelection){
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder,currentRecipe)
                true
            }else{
                applySelection(holder,currentRecipe)
                true
            }
        }

    }

    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }

    private fun saveItemStateOnScroll(currentRecipe: FavoriteEntity, holder: FavoriteViewHolder){
        if (selectedRecipes.contains(currentRecipe)) {
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.color_primary)
        } else {
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
    }

    private fun applySelection(holder: FavoriteViewHolder, currentRecipe: FavoriteEntity) {
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModeTitle()
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.color_primary)
            applyActionModeTitle()
        }
    }

    private fun changeRecipeStyle(holder: FavoriteViewHolder, backgroundColor: Int, strokeColor: Int) {
        holder.binding.favoriteRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.binding.favoriteRowCardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    private fun applyActionModeTitle() {
        when (selectedRecipes.size) {
            0 -> {
                mActionMode.finish()
                multiSelection = false
            }
            1 -> {
                mActionMode.title = "${selectedRecipes.size} item selected"
            }
            else -> {
                mActionMode.title = "${selectedRecipes.size} items selected"
            }
        }
    }

    fun setData(newFavoriteRecipes: List<FavoriteEntity>) {
        val favoriteRecipesDiffUtil =
            RecipesDiffUtil(favoriteRecipes, newFavoriteRecipes)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipes = newFavoriteRecipes
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor =
            ContextCompat.getColor(requireActivity, color)
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.favorites_contextual_menu,menu)
        mActionMode = mode!!
        applyStatusBarColor(R.color.contextualActionBarColor)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if (item?.itemId == R.id.delete_favorite_recipe_menu) {
            selectedRecipes.forEach {
                mainViewModel.deleteFavoriteRecipe(it)
            }
            showSnackBar("${selectedRecipes.size} Recipe/s removed.")

            multiSelection = false
            selectedRecipes.clear()
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        myViewHolder.forEach{holder->
            changeRecipeStyle(holder,R.color.cardBackgroundColor,R.color.strokeColor)
        }
        multiSelection = false
        selectedRecipes.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay") {}
            .show()
    }

    fun clearContextualActionMode(){
        if(this::mActionMode.isInitialized){
            mActionMode.finish()
        }
    }

}