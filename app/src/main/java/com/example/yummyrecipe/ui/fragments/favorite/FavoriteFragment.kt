package com.example.yummyrecipe.ui.fragments.favorite

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yummyrecipe.R
import com.example.yummyrecipe.adapters.FavoritesAdapter
import com.example.yummyrecipe.databinding.FragmentFavoriteBinding
import com.example.yummyrecipe.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private val mainViewModel : MainViewModel by viewModels()
    private val adapter : FavoritesAdapter by lazy{ FavoritesAdapter(requireActivity(),mainViewModel)}

    private var _binding: FragmentFavoriteBinding ?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.mAdapter = adapter

        setupRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.favorite_menu,menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId == R.id.deleteAll_favorite_recipes_menu){
                    deleteAllData()
                }
                return true
            }

        },viewLifecycleOwner,Lifecycle.State.RESUMED)

    }

    private fun setupRecyclerView() {
        binding.favoriteRecipesRecyclerView.adapter = adapter
        binding.favoriteRecipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun deleteAllData(){

        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_->
            mainViewModel.deleteAllFavoriteRecipes()
            Toast.makeText(requireContext(),"Successfully Deleted!", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("No"){_,_->}
        builder.setTitle("Delete all data")
        builder.setMessage("Are you sure you want to delete all data ?")
        builder.create().show()

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        adapter.clearContextualActionMode()
    }
}