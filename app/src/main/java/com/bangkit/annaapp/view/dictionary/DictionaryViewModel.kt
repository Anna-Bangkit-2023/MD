package com.bangkit.annaapp.view.dictionary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.annaapp.data.Repository
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.data.remote.response.DictionaryResponse
import kotlinx.coroutines.launch

class DictionaryViewModel(private val repository: Repository) : ViewModel() {
    private val _wordDetails = MutableLiveData<DictionaryResponse?>()
    val wordDetails: LiveData<DictionaryResponse?> = _wordDetails

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun searchWord(word: String) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = repository.getWordDetails(word)) {
                is ResultState.Success -> {
                    val firstItem = result.data.firstOrNull()
                    _wordDetails.value = firstItem
                    _isLoading.value = false
                }

                is ResultState.Error -> {
                    _errorMessage.value = result.error
                    _isLoading.value = false
                }

                else -> {}
            }
        }
    }
}