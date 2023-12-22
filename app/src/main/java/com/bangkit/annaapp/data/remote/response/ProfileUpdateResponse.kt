package com.bangkit.annaapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class ProfileUpdateResponse(

	@field:SerializedName("photo_profile")
	val photoProfile: String,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("user")
	val user: UserUpdate
)

data class UserUpdate(

	@field:SerializedName("role")
	val role: String,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("phone")
	val phone: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("photo")
	val photo: String,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: Any,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("membership")
	val membership: String,

	@field:SerializedName("type")
	val type: String,

	@field:SerializedName("email")
	val email: String
)
