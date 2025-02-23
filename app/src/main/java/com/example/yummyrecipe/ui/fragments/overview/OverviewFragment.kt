package com.example.yummyrecipe.ui.fragments.overview


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.load
import com.example.yummyrecipe.R
import com.example.yummyrecipe.bindingadapters.RecipesRowBinding
import com.example.yummyrecipe.databinding.FragmentOverviewBinding
import com.example.yummyrecipe.model.Result
import com.example.yummyrecipe.util.Constants.Companion.RECIPE_RESULT_KEY

class OverviewFragment : Fragment() {
    private lateinit var binding : FragmentOverviewBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOverviewBinding.inflate(inflater,container,false)

        val args = arguments
        val myBundle : Result =args!!.getParcelable<Result>(RECIPE_RESULT_KEY) as Result

        binding.mainImageView.load(myBundle.image)
        binding.titleTextView.text = myBundle.title
        binding.likesTextView.text = myBundle.aggregateLikes.toString()
        binding.timeTextView.text = myBundle.readyInMinutes.toString()

        RecipesRowBinding.parseHtml(binding.summaryTextView,myBundle.summary)

        updateColors(myBundle.vegetarian,binding.vegetarianTextView,binding.vegetarianImageView)
        updateColors(myBundle.vegan,binding.veganTextView,binding.veganImageView)
        updateColors(myBundle.glutenFree,binding.glutenFreeTextView,binding.glutenFreeImageView)
        updateColors(myBundle.dairyFree,binding.dairyFreeTextView,binding.dairyFreeImageView)
        updateColors(myBundle.veryHealthy,binding.healthyTextView,binding.healthyImageView)
        updateColors(myBundle.cheap,binding.cheapTextView,binding.cheapImageView)

        return binding.root
    }

    private fun updateColors(stateIsOn:Boolean,textView:TextView,imageView: ImageView){
        if (stateIsOn){
            imageView.setColorFilter(ContextCompat.getColor(requireContext(),R.color.green))
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
        }
    }

}