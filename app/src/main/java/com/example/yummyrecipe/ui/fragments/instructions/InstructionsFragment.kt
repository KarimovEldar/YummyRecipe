package com.example.yummyrecipe.ui.fragments.instructions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.yummyrecipe.databinding.FragmentInstructionsBinding
import com.example.yummyrecipe.model.Result
import com.example.yummyrecipe.util.Constants.Companion.RECIPE_RESULT_KEY

class InstructionsFragment : Fragment() {
    private lateinit var binding : FragmentInstructionsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInstructionsBinding.inflate(inflater,container,false)

        val args = arguments
        val myBundle : Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        binding.instructionsWebView.webViewClient = object : WebViewClient(){}
        val webSiteUrl : String = myBundle!!.sourceUrl
        binding.instructionsWebView.loadUrl(webSiteUrl)

        return binding.root
    }

}