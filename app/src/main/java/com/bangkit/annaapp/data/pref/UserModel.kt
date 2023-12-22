package com.bangkit.annaapp.data.pref

data class UserModel(
    val name: String,
    val email: String,
    val photoProfile: String?,
    val id: Int,
    val accessToken: String,
    val isLogin: Boolean = false
)
