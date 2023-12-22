package com.bangkit.annaapp.view.editprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.bangkit.annaapp.data.Repository
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.data.pref.UserModel
import com.bangkit.annaapp.data.remote.response.ProfileUpdateResponse
import java.io.File

class EditProfileViewModel(private val repository: Repository) : ViewModel() {
    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun updateProfile(
        imageFile: File?,
        name: String?
    ): LiveData<ResultState<ProfileUpdateResponse>> {
        if (imageFile == null && name == null) {
            return liveData { emit(ResultState.Error("No changes to update")) }
        }
        return repository.updateProfile(name, imageFile)
    }
}