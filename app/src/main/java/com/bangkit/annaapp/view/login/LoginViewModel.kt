package com.bangkit.annaapp.view.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.annaapp.data.Repository
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.data.pref.UserModel
import com.bangkit.annaapp.data.remote.response.LoginResponse
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: Repository) : ViewModel() {

    private val _loginResult = MutableLiveData<ResultState<LoginResponse>>()
    val loginResult: LiveData<ResultState<LoginResponse>> = _loginResult

    private val _isButtonEnabled = MutableLiveData<Boolean>()
    val isButtonEnabled: LiveData<Boolean>
        get() = _isButtonEnabled

    init {
        _isButtonEnabled.value = false
    }

    fun checkText(email: String, password: String) {
        val isButtonEnabled = isEmailValid(email) && password.length >= 8
        _isButtonEnabled.value = isButtonEnabled
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = ResultState.Loading
            when (val result = repository.login(email, password)) {
                is ResultState.Success -> {
                    // save user session
                    val userModel = UserModel(
                        name = result.data.user.name,
                        email = result.data.user.email,
                        photoProfile = result.data.photoProfile as? String,
                        id = result.data.user.id,
                        accessToken = result.data.accessToken,
                        isLogin = true
                    )
                    saveSession(userModel) {
                        _loginResult.value = ResultState.Success(result.data)
                    }
                    val currentUser = repository.getSession().firstOrNull()
                    Log.d("Login", "Current user token: ${currentUser?.accessToken}")
                }

                is ResultState.Error -> _loginResult.value = result
                else -> {}
            }
        }
    }

    private fun saveSession(user: UserModel, onSessionSaved: () -> Unit) {
        viewModelScope.launch {
            repository.saveSession(user, onSessionSaved)
        }
    }
}