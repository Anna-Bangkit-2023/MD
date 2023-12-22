package com.bangkit.annaapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class MessageResponse(

	@field:SerializedName("data")
	val data: MessageData,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class MessageData(

	@field:SerializedName("room_chat_id")
	val roomChatId: Int,

	@field:SerializedName("file")
	val file: Any,

	@field:SerializedName("updated_at")
	val updatedAt: String,

	@field:SerializedName("receiver_id")
	val receiverId: Int,

	@field:SerializedName("created_at")
	val createdAt: String,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("sender_id")
	val senderId: Int
)
