package com.bangkit.annaapp.di

import android.content.Context
import com.bangkit.annaapp.data.pref.UserPreference
import com.bangkit.annaapp.data.pref.dataStore
import com.bangkit.annaapp.data.Repository
import com.bangkit.annaapp.data.remote.retrofit.ApiConfig
import com.bangkit.annaapp.data.remote.retrofit.ApiConfigDictionary

object Injection {
    fun provideRepository(context: Context): Repository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val apiServiceDictionary =
            ApiConfigDictionary.getApiService()
        return Repository.getInstance(
            pref,
            apiService,
            apiServiceDictionary
        )
    }
}