package com.bangkit.annaapp.view.chathistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bangkit.annaapp.data.Repository
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.data.pref.UserModel
import com.bangkit.annaapp.data.remote.response.CreateRoomResponse
import com.bangkit.annaapp.data.remote.response.DeleteRoomResponse
import com.bangkit.annaapp.data.remote.response.ListChatItem

class ChatHistoryViewModel(private val repository: Repository) : ViewModel() {
    val chat: LiveData<PagingData<ListChatItem>> =
        repository.getChatPagingSource().cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun createRoom(title: String): LiveData<ResultState<CreateRoomResponse>> = liveData {
        emit(ResultState.Loading)
        val result = repository.createRoom(title)
        emit(result)
    }

    fun deleteChatRoom(id: Int): LiveData<ResultState<DeleteRoomResponse>> = liveData {
        emit(ResultState.Loading)
        val result = repository.deleteChatRoom(id)
        emit(result)
    }
}