package com.bangkit.annaapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.annaapp.R
import com.bangkit.annaapp.data.remote.response.MessagesItem
import com.bangkit.annaapp.databinding.ItemChatBinding

class ChatAdapter(
    private val currentUserId: Int,
    private val onAudioPlayClick: (String, Int) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private var messageList: List<MessagesItem> = listOf()
    private var currentlyPlayingAudioIndex: Int? = null

    inner class ChatViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(messageData: MessagesItem, isPlaying: Boolean) {
            val fileString = messageData.file?.toString()
            val isAudioMessage = fileString?.startsWith("audio/") ?: false

            binding.leftChatView.visibility = View.GONE
            binding.rightChatView.visibility = View.GONE
            binding.rightAudioView.visibility = View.GONE

            if (isAudioMessage) {
                val audioFileName = fileString?.split("/")?.last()
                binding.rightAudioView.visibility =
                    if (messageData.senderId == currentUserId) View.VISIBLE else View.GONE
                binding.btnPlay.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)
                binding.btnPlay.setOnClickListener {
                    if (audioFileName != null) {
                        onAudioPlayClick(audioFileName, adapterPosition)
                    }
                }
            } else {
                if (messageData.senderId == currentUserId) {
                    binding.rightChatView.visibility = View.VISIBLE
                    binding.rightChatTextView.text = messageData.message
                } else {
                    binding.leftChatView.visibility = View.VISIBLE
                    binding.leftChatTextView.text = messageData.message
                }
            }
        }
    }

    fun updatePlayPauseIcon(position: Int, isPlaying: Boolean) {
        if (currentlyPlayingAudioIndex != null && currentlyPlayingAudioIndex != position) {
            notifyItemChanged(currentlyPlayingAudioIndex!!)
        }
        currentlyPlayingAudioIndex = if (isPlaying) position else null
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemChatBinding.inflate(inflater, parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val isPlaying = position == currentlyPlayingAudioIndex
        holder.bind(messageList[position], isPlaying)
    }

    fun setMessages(newMessages: List<MessagesItem>) {
        messageList = newMessages
        notifyDataSetChanged()
    }

    fun addMessage(newMessage: MessagesItem) {
        messageList = messageList + newMessage
        notifyItemInserted(messageList.size - 1)
    }

    override fun getItemCount(): Int = messageList.size
}