package com.bangkit.annaapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.annaapp.data.Repository
import com.bangkit.annaapp.di.Injection
import com.bangkit.annaapp.view.chat.ChatViewModel
import com.bangkit.annaapp.view.chathistory.ChatHistoryViewModel
import com.bangkit.annaapp.view.dictionary.DictionaryViewModel
import com.bangkit.annaapp.view.editprofile.EditProfileViewModel
import com.bangkit.annaapp.view.login.LoginViewModel
import com.bangkit.annaapp.view.main.MainViewModel
import com.bangkit.annaapp.view.profile.ProfileViewModel
import com.bangkit.annaapp.view.register.RegisterViewModel

class ViewModelFactory(private val repository: Repository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }

            modelClass.isAssignableFrom(EditProfileViewModel::class.java) -> {
                EditProfileViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ChatHistoryViewModel::class.java) -> {
                ChatHistoryViewModel(repository) as T
            }

            modelClass.isAssignableFrom(ChatViewModel::class.java) -> {
                ChatViewModel(repository) as T
            }

            modelClass.isAssignableFrom(DictionaryViewModel::class.java) -> {
                DictionaryViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}