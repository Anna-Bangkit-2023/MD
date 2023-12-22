package com.bangkit.annaapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bangkit.annaapp.data.pref.UserModel
import com.bangkit.annaapp.data.Repository

class MainViewModel(private val repository: Repository): ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

}