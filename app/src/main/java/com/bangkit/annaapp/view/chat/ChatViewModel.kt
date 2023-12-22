package com.bangkit.annaapp.view.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.bangkit.annaapp.data.Repository
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.data.pref.UserModel
import com.bangkit.annaapp.data.remote.response.ChatEditTitleResponse
import com.bangkit.annaapp.data.remote.response.Data
import com.bangkit.annaapp.data.remote.response.MessageData
import com.bangkit.annaapp.data.remote.response.MessageResponse
import com.bangkit.annaapp.data.remote.response.MessagesItem
import com.bangkit.annaapp.data.remote.response.RoomData
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.io.File

class ChatViewModel(private val repository: Repository) : ViewModel() {
    private val _chatRoomDetails = MutableLiveData<Data>()
    val chatRoomDetails: LiveData<Data> = _chatRoomDetails

    val currentUser: LiveData<UserModel> = liveData {
        emitSource(repository.getSession().asLiveData())
    }

    fun getChatRoomDetails(chatRoomId: Int) {
        viewModelScope.launch {
            val result = repository.getChatRoom(chatRoomId.toString())
            if (result is ResultState.Success) {
                _chatRoomDetails.value = result.data.data
            } else {
                // Handle error case
            }
        }
    }

    fun updateChatTitle(
        chatRoomId: Int,
        title: String
    ): LiveData<ResultState<ChatEditTitleResponse>> = liveData {
        emit(ResultState.Loading)
        val result = repository.updateChatTitle(chatRoomId, title)
        emit(result)
    }

    fun sendMessage(
        roomChatId: Int,
        message: String?,
        audioFile: File?
    ): LiveData<ResultState<MessagesItem>> = liveData {
        Log.d(
            "ChatViewModel",
            "sendMessage called, message: $message, audioFile: ${audioFile?.path}"
        )
        emit(ResultState.Loading)

        val response = if (!message.isNullOrEmpty()) {
            repository.sendMessage(roomChatId, message, null)
        } else if (audioFile != null) {
            repository.sendMessage(roomChatId, null, audioFile)
        } else {
            emit(ResultState.Error("No message or audio to send"))
            return@liveData
        }

        when (response) {
            is ResultState.Success -> {
                val messageData = response.data.data
                val messagesItem = MessagesItem(
                    roomChatId = messageData.roomChatId,
                    file = messageData.file ?: "",
                    updatedAt = messageData.updatedAt,
                    receiverId = messageData.receiverId,
                    createdAt = messageData.createdAt,
                    id = messageData.id,
                    message = messageData.message ?: "",
                    senderId = messageData.senderId
                )
                emit(ResultState.Success(messagesItem))
            }

            is ResultState.Error -> emit(ResultState.Error(response.error))
            else -> {}
        }
    }

    fun reloadChatRoom(chatRoomId: Int) {
        viewModelScope.launch {
            val result = repository.getChatRoom(chatRoomId.toString())
            if (result is ResultState.Success) {
                _chatRoomDetails.postValue(result.data.data)
            }
            // Handle error case
        }
    }
}