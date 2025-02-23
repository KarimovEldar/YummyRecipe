package com.example.yummyrecipe.model


import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("results")
    val results: List<Result>,
)