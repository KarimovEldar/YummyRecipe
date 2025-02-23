package com.example.yummyrecipe.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(value: T) {
            observer.onChanged(value)
            removeObserver(this)
        }
    })
}

class MyExtensionFunction (){
    companion object {
        val String.asCapitalized
            get() = lowercase().replaceFirstChar { it.titlecase() }
    }
}