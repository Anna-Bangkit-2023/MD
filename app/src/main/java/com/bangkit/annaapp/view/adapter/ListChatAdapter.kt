package com.bangkit.annaapp.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.annaapp.data.remote.response.ListChatItem
import com.bangkit.annaapp.databinding.ItemHistoryBinding
import com.bangkit.annaapp.utils.formatDateString

class ListChatAdapter(
    private val clickListener: OnChatItemClickListener,
    private val deleteListener: OnChatItemDeletedListener
) : PagingDataAdapter<ListChatItem, ListChatAdapter.ChatViewHolder>(DIFF_CALLBACK) {

    interface OnChatItemClickListener {
        fun onChatItemClick(chatRoomId: Int)
    }

    interface OnChatItemDeletedListener {
        fun onChatItemDeleted(chatRoomId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding, clickListener, deleteListener)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class ChatViewHolder(
        private val binding: ItemHistoryBinding,
        private val clickListener: OnChatItemClickListener,
        private val deleteListener: OnChatItemDeletedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ListChatItem) {
            val formattedDate = formatDateString(item.updatedAt)
            binding.tvDate.text = "Date: $formattedDate"
            binding.tvTitle.text = item.title

            itemView.setOnClickListener {
                clickListener.onChatItemClick(item.id)
            }

            binding.ivDelete.setOnClickListener {
                deleteListener.onChatItemDeleted(item.id)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListChatItem>() {
            override fun areItemsTheSame(oldItem: ListChatItem, newItem: ListChatItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListChatItem, newItem: ListChatItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
