package com.example.yummyrecipe.ui.fragments.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yummyrecipe.adapters.IngredientsAdapter
import com.example.yummyrecipe.databinding.FragmentIngredientsBinding
import com.example.yummyrecipe.model.Result
import com.example.yummyrecipe.util.Constants.Companion.RECIPE_RESULT_KEY

class IngredientsFragment : Fragment() {
    private val adapter by lazy { IngredientsAdapter() }
    private lateinit var binding:FragmentIngredientsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIngredientsBinding.inflate(inflater,container,false)

        val args = arguments
        val myBundle : Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        setUpRecyclerView()
        myBundle?.extendedIngredients?.let { adapter.setData(it) }

        return binding.root
    }

    fun setUpRecyclerView(){
        binding.ingredientsRecyclerview.adapter = adapter
        binding.ingredientsRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

}