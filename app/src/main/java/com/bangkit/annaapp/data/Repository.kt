package com.bangkit.annaapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.bangkit.annaapp.data.pref.UserModel
import com.bangkit.annaapp.data.pref.UserPreference
import com.bangkit.annaapp.data.remote.response.ChatEditTitleResponse
import com.bangkit.annaapp.data.remote.response.ChatRoomResponse
import com.bangkit.annaapp.data.remote.response.CreateRoomResponse
import com.bangkit.annaapp.data.remote.response.DeleteRoomResponse
import com.bangkit.annaapp.data.remote.response.DictionaryResponse
import com.bangkit.annaapp.data.remote.response.ErrorResponse
import com.bangkit.annaapp.data.remote.response.ListChatItem
import com.bangkit.annaapp.data.remote.response.LoginResponse
import com.bangkit.annaapp.data.remote.response.LogoutResponse
import com.bangkit.annaapp.data.remote.response.MessageResponse
import com.bangkit.annaapp.data.remote.response.ProfileUpdateResponse
import com.bangkit.annaapp.data.remote.response.RegisterResponse
import com.bangkit.annaapp.data.remote.retrofit.ApiService
import com.bangkit.annaapp.data.remote.retrofit.ApiServiceDictionary
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class Repository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val apiServiceDictionary: ApiServiceDictionary
) {

    suspend fun saveSession(user: UserModel, onComplete: () -> Unit) {
        userPreference.saveSession(user)
        onComplete.invoke()
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String,
        passwordConfirm: String
    ): ResultState<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, phone, password, passwordConfirm)
            if (response.error != true) {
                ResultState.Success(response)
            } else {
                ResultState.Error(response.message ?: "Registration failed")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            ResultState.Error(errorResponse.message ?: "Registration failed")
        } catch (e: Exception) {
            ResultState.Error("Registration failed")
        }
    }

    suspend fun login(email: String, password: String): ResultState<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            if (!response.error) {
                ResultState.Success(response)
            } else {
                ResultState.Error(response.message)
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            ResultState.Error(errorResponse.message ?: "Login failed http")
        } catch (e: Exception) {
            ResultState.Error("Login failed exc")
        }
    }

    suspend fun logout(
    ): ResultState<LogoutResponse> {
        return try {
            val user = userPreference.getSession().first()
            val response = apiService.logout("Bearer ${user.accessToken}")
            if (!response.error) {
                userPreference.logout()
                ResultState.Success(response)
            } else {
                ResultState.Error(response.message ?: "Logout failed")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            ResultState.Error(errorResponse.message ?: "Logout failed")
        } catch (e: Exception) {
            ResultState.Error("Logout failed")
        }
    }

    fun updateProfile(
        name: String?, imageFile: File?
    ): LiveData<ResultState<ProfileUpdateResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val user = userPreference.getSession().first()
            val authorization = "Bearer ${user.accessToken}"

            val namePart = name?.toRequestBody("text/plain".toMediaType())
            val photoPart = imageFile?.let {
                val requestImageFile = it.asRequestBody("image/jpeg".toMediaType())
                MultipartBody.Part.createFormData("photo", it.name, requestImageFile)
            }

            val response = apiService.updateProfile(authorization, namePart, photoPart)
            if (!response.error) {
                userPreference.saveSession(
                    UserModel(
                        name = response.user.name,
                        email = response.user.email,
                        photoProfile = response.photoProfile,
                        id = response.user.id,
                        accessToken = user.accessToken,
                        isLogin = true
                    )
                )
                emit(ResultState.Success(response))
            } else {
                emit(ResultState.Error(response.message))
            }
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "An error occurred"))
        }
    }

    suspend fun createRoom(title: String): ResultState<CreateRoomResponse> {
        return try {
            val user = userPreference.getSession().first()
            val authorization = "Bearer ${user.accessToken}"
            val response = apiService.createRoom(authorization, title)
            if (!response.error) {
                ResultState.Success(response)
            } else {
                ResultState.Error(response.message)
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Error creating room")
        }
    }

    suspend fun getChatRoom(id: String): ResultState<ChatRoomResponse> {
        return try {
            val user = userPreference.getSession().first()
            val authorization = "Bearer ${user.accessToken}"
            val response = apiService.getChatRoom(authorization, id)
            if (!response.error) {
                ResultState.Success(response)
            } else {
                ResultState.Error(response.message)
            }
        } catch (e: HttpException) {
            Log.e("DetailActivity", "API Error: ${e.message}")
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            ResultState.Error(errorResponse.message ?: "Load Chat Room failed")
        } catch (e: Exception) {
            ResultState.Error("Load Chat Room failed")
        }
    }

    suspend fun deleteChatRoom(id: Int): ResultState<DeleteRoomResponse> {
        return try {
            val user = userPreference.getSession().first()
            val authorization = "Bearer ${user.accessToken}"
            val response = apiService.deleteChatRoom(authorization, id)
            if (!response.error) {
                ResultState.Success(response)
            } else {
                ResultState.Error(response.message)
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Error deleting room")
        }
    }

    fun getChatPagingSource(): LiveData<PagingData<ListChatItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                ChatPagingSource(userPreference, apiService)
            }
        ).liveData
    }

    suspend fun updateChatTitle(
        chatRoomId: Int,
        title: String
    ): ResultState<ChatEditTitleResponse> {
        return try {
            val user = userPreference.getSession().first()
            val response = apiService.updateTitle("Bearer ${user.accessToken}", chatRoomId, title)
            if (!response.error) {
                ResultState.Success(response)
            } else {
                ResultState.Error(response.message)
            }
        } catch (e: Exception) {
            ResultState.Error(e.message ?: "Error updating title")
        }
    }

    suspend fun sendMessage(
        roomChatId: Int,
        message: String?,
        audioFile: File?
    ): ResultState<MessageResponse> {
        return try {
            val user = userPreference.getSession().first()
            val authorization = "Bearer ${user.accessToken}"
            val receiverIdPart = "2".toRequestBody("text/plain".toMediaType())

            val messagePart = message?.toRequestBody("text/plain".toMediaType())

            val audioPart = audioFile?.let {
                Log.d("ChatRepository", "Audio file: ${it.name}, Size: ${it.length()}")
                val requestAudioFile = it.asRequestBody("audio/x-wav".toMediaType())
                MultipartBody.Part.createFormData("file", it.name, requestAudioFile)
            }

            Log.d("ChatRepository", "Sending audio file: ${audioFile?.name}")
            val response = apiService.sendMessage(
                authorization,
                receiverIdPart,
                roomChatId.toString().toRequestBody("text/plain".toMediaType()),
                messagePart,
                audioPart
            )
            ResultState.Success(response)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error sending message", e)
            ResultState.Error(e.message ?: "Error sending message")
        }
    }

    suspend fun getWordDetails(word: String): ResultState<List<DictionaryResponse>> {
        return try {
            val response = apiServiceDictionary.getWordDetails(word)
            if (response.isSuccessful) {
                response.body()?.let {
                    ResultState.Success(it)
                } ?: ResultState.Error("No data")
            } else {
                ResultState.Error("API error: ${response.errorBody()?.string() ?: "Unknown error"}")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            ResultState.Error(errorResponse.message ?: "get word details failed http")
        } catch (e: Exception) {
            Log.e("Repository", "getword details failed e", e)
            ResultState.Error("getword details failed exc")
        }
    }

    companion object {
        @Volatile
        private var instance: Repository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService,
            apiServiceDictionary: ApiServiceDictionary
        ): Repository =
            instance ?: synchronized(this) {
                instance ?: Repository(userPreference, apiService, apiServiceDictionary)
            }.also { instance = it }
    }
}