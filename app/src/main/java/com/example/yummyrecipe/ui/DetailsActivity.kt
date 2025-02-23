package com.example.yummyrecipe.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.navArgs
import com.example.yummyrecipe.R
import com.example.yummyrecipe.adapters.PagerAdapter
import com.example.yummyrecipe.data.local.entities.FavoriteEntity
import com.example.yummyrecipe.databinding.ActivityDetailsBinding
import com.example.yummyrecipe.ui.fragments.ingredients.IngredientsFragment
import com.example.yummyrecipe.ui.fragments.instructions.InstructionsFragment
import com.example.yummyrecipe.ui.fragments.overview.OverviewFragment
import com.example.yummyrecipe.util.Constants.Companion.RECIPE_RESULT_KEY
import com.example.yummyrecipe.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailsBinding
    private val args by navArgs<DetailsActivityArgs>()
    private val mainViewModel: MainViewModel by viewModels()

    private var recipeSaved = false
    private var savedRecipeId = 0

    private lateinit var menuItem : MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.detailsToolbar)
        binding.detailsToolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fragments = ArrayList<Fragment>()
        fragments.add(OverviewFragment())
        fragments.add(IngredientsFragment())
        fragments.add(InstructionsFragment())

        val titles = ArrayList<String>()
        titles.add("Overview")
        titles.add("Ingredient")
        titles.add("Instructions")

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY,args.result)

        val pagerAdapter = PagerAdapter(
            resultBundle,
            fragments,
            this
        )

        binding.detailsViewPager.apply {
            adapter = pagerAdapter
        }

        TabLayoutMediator(binding.detailTabLayout,binding.detailsViewPager){tab,position->
            tab.text = titles[position]
        }.attach()

        val menuHost: MenuHost = this
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.details_menu,menu)
                menuItem = menu.findItem(R.id.menu_save_favorite)
                checkSavedRecipes(menuItem)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }

        },this, Lifecycle.State.RESUMED)

    }

    fun checkSavedRecipes(menuItem : MenuItem){
        mainViewModel.readFavoriteEntity.observe(this){favoritesEntity->
            try {
                for(saveRecipe in favoritesEntity){
                    if (saveRecipe.result.id == args.result.id){
                        changeMenuItemColor(menuItem,R.color.red)
                        savedRecipeId = saveRecipe.id
                        recipeSaved = true
                    }
                }
            }catch (e:Exception){
                Log.d("DetailsActivity",e.message.toString())
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }else if(item.itemId == R.id.menu_save_favorite && !recipeSaved){
            saveToFavorites(item)
        }else if(item.itemId == R.id.menu_save_favorite && recipeSaved){
            removeFromFavorites(item)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveToFavorites(item: MenuItem) {
        val favoritesEntity =
            FavoriteEntity(
                0,
                args.result
            )
        mainViewModel.insertFavoriteRecipes(favoritesEntity)
        changeMenuItemColor(item,R.color.red)
        showSnackBar("RecipesSaved.")
        recipeSaved = true
    }

    private fun removeFromFavorites(item: MenuItem){

        val favoriteEntity =
            FavoriteEntity(
                savedRecipeId,
                args.result
            )
        mainViewModel.deleteFavoriteRecipe(favoriteEntity)
        changeMenuItemColor(item,R.color.white)
        showSnackBar("Removed from Favorites.")
        recipeSaved = false

    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.detailsLayout,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay"){}.show()
    }

    private fun changeMenuItemColor(item: MenuItem, color: Int) {
        item.icon?.setTint(ContextCompat.getColor(this, color))
    }

    override fun onDestroy() {
        super.onDestroy()
        changeMenuItemColor(menuItem,R.color.white)
    }

}