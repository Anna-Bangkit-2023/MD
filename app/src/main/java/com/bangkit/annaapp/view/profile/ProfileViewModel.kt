package com.bangkit.annaapp.view.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.annaapp.data.Repository
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.data.pref.UserModel
import com.bangkit.annaapp.data.remote.response.LogoutResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: Repository) : ViewModel() {

    private val _logoutResult = MutableLiveData<ResultState<LogoutResponse>>()
    val logoutResult: LiveData<ResultState<LogoutResponse>> = _logoutResult

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            val currentUser = repository.getSession().firstOrNull()
            Log.d("Logout", "Current user token: ${currentUser?.accessToken}")

            if (currentUser != null && currentUser.accessToken.isNotEmpty()) {
                repository.logout()
                val result = repository.logout()
//                _logoutResult.postValue(result)
            } else {
                Log.d("Logout", "No user is logged in, cannot perform logout.")
            }
        }
    }
}