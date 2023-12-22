package com.bangkit.annaapp.data.remote.retrofit

import com.bangkit.annaapp.data.remote.response.ChatEditTitleResponse
import com.bangkit.annaapp.data.remote.response.ChatResponse
import com.bangkit.annaapp.data.remote.response.ChatRoomResponse
import com.bangkit.annaapp.data.remote.response.CreateRoomResponse
import com.bangkit.annaapp.data.remote.response.DeleteRoomResponse
import com.bangkit.annaapp.data.remote.response.DictionaryResponse
import com.bangkit.annaapp.data.remote.response.LoginResponse
import com.bangkit.annaapp.data.remote.response.LogoutResponse
import com.bangkit.annaapp.data.remote.response.MessageResponse
import com.bangkit.annaapp.data.remote.response.ProfileUpdateResponse
import com.bangkit.annaapp.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirm: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") authorization: String
    ): LogoutResponse

    @Multipart
    @POST("user")
    suspend fun updateProfile(
        @Header("Authorization") authorization: String,
        @Part("name") name: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): ProfileUpdateResponse

    @FormUrlEncoded
    @POST("room")
    suspend fun createRoom(
        @Header("Authorization") authorization: String,
        @Field("title") title: String
    ): CreateRoomResponse

    @GET("rooms")
    suspend fun getChatRooms(
        @Header("Authorization") authorization: String
    ): ChatResponse

    @GET("room/{id}")
    suspend fun getChatRoom(
        @Header("Authorization") authorization: String,
        @Path("id") id: String
    ): ChatRoomResponse

    @DELETE("room/{id}")
    suspend fun deleteChatRoom(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    ): DeleteRoomResponse

    @FormUrlEncoded
    @POST("room/{id}")
    suspend fun updateTitle(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int,
        @Field("title") title: String
    ): ChatEditTitleResponse

    @Multipart
    @POST("message")
    suspend fun sendMessage(
        @Header("Authorization") authorization: String,
        @Part("receiver_id") receiverId: RequestBody,
        @Part("room_chat_id") roomChatId: RequestBody,
        @Part("message") message: RequestBody?,
        @Part audio: MultipartBody.Part?
    ): MessageResponse
}

interface ApiServiceDictionary {
    @GET("{word}")
    suspend fun getWordDetails(
        @Path("word") word: String,
    ): retrofit2.Response<List<DictionaryResponse>>
}
