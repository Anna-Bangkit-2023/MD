package com.bangkit.annaapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class LogoutResponse(

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)