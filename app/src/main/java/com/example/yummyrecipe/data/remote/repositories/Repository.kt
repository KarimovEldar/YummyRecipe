package com.example.yummyrecipe.data.remote.repositories

import com.example.yummyrecipe.data.local.data.LocalDataSource
import com.example.yummyrecipe.data.remote.data.RemoteDataSource
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
class Repository @Inject constructor(
    remoteDataSource: RemoteDataSource,
    localDataSource: LocalDataSource
) {

    val remote = remoteDataSource
    val local = localDataSource

}