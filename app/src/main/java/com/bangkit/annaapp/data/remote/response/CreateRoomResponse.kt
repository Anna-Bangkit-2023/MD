package com.bangkit.annaapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class CreateRoomResponse(

	@field:SerializedName("data")
	val data: RoomData,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("status")
	val status: String
)

data class RoomData(

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("user_id")
	val userId: Int,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String
)
