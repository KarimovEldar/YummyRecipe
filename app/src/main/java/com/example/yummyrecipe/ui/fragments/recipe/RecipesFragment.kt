package com.example.yummyrecipe.ui.fragments.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yummyrecipe.R
import com.example.yummyrecipe.adapters.RecipesAdapter
import com.example.yummyrecipe.databinding.FragmentRecipesBinding
import com.example.yummyrecipe.util.NetworkListener
import com.example.yummyrecipe.util.NetworkResult
import com.example.yummyrecipe.util.observeOnce
import com.example.yummyrecipe.viewmodels.MainViewModel
import com.example.yummyrecipe.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment() , SearchView.OnQueryTextListener{

    private var _binding: FragmentRecipesBinding ?= null
    private val binding get() = _binding!!

    private val args by navArgs<RecipesFragmentArgs>()

    private val adapter by lazy { RecipesAdapter() }
    private lateinit var mainViewModel : MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel

    private lateinit var networkListener: NetworkListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recipesViewModel = ViewModelProvider(requireActivity())[RecipesViewModel::class.java]

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel

        setUpRecyclerView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner){
            recipesViewModel.backOnline = it
        }

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect{status ->
                    Log.d("NetworkListener",status.toString())
                    recipesViewModel.networkStatus = status
                    recipesViewModel.showNetworkStatus()
                    readDatabase()
                }
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.recipes_menu,menu)

                val search = menu.findItem(R.id.menu_search)
                val searchView = search.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@RecipesFragment)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.floatingActionButton.setOnClickListener {
            if(recipesViewModel.networkStatus){
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet)
            }else{
                recipesViewModel.showNetworkStatus()
            }
        }

        return binding.root
    }

    private fun setUpRecyclerView(){
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
    }

    private fun readDatabase(){
        lifecycleScope.launch {//We use lifecycleScope -> Because readDatabase isn't suspend function.
            mainViewModel.readDatabase.observeOnce(viewLifecycleOwner){database->
                if (database.isNotEmpty() && !args.backFromBottomSheet){
                    Log.d("RecipesFragment","readDatabase called!")
                    adapter.setData(database[0].recipe)
                    hideShimmerEffect()
                    binding.shimmerLayout.visibility = View.INVISIBLE
                }else{
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData(){
        Log.d("RecipesFragment","requestApiData called!")
        mainViewModel.getAllRecipes(recipesViewModel.applyQueries())
        mainViewModel.recipesResponse.observe(viewLifecycleOwner){response->
            when(response){
                is NetworkResult.Success -> {
                    hideShimmerEffect()
                    response.data?.let{ adapter.setData(it) }
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            searchApiData(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun searchApiData(searchQuery:String){
        showShimmerEffect()
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchRecipesResponse.observe(viewLifecycleOwner){response->
            when(response){
                is NetworkResult.Success ->{
                    hideShimmerEffect()
                    val foodRecipe = response.data
                    foodRecipe?.let { adapter.setData(it)}
                }
                is NetworkResult.Error -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffect()
                }
            }

        }
    }

    private fun loadDataFromCache(){
        lifecycleScope.launch {
            mainViewModel.readDatabase.observe(viewLifecycleOwner){database->
                if(database.isNotEmpty()){
                    adapter.setData(database[0].recipe)
                }
            }
        }
    }

    private fun showShimmerEffect(){
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.visibility = View.VISIBLE
    }

    private fun hideShimmerEffect(){
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}