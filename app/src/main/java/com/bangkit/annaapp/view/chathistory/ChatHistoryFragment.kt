package com.bangkit.annaapp.view.chathistory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.annaapp.R
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.databinding.FragmentChatHistoryBinding
import com.bangkit.annaapp.view.ViewModelFactory
import com.bangkit.annaapp.view.adapter.ListChatAdapter
import com.bangkit.annaapp.view.adapter.LoadingStateAdapter

class ChatHistoryFragment : Fragment() {

    private var _binding: FragmentChatHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatHistoryViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapter: ListChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = context?.getString(R.string.chat)
        (activity as? AppCompatActivity)?.supportActionBar?.title = title

        binding.rvChat.layoutManager = LinearLayoutManager(context)
        adapter = ListChatAdapter(
            clickListener = object : ListChatAdapter.OnChatItemClickListener {
                override fun onChatItemClick(chatRoomId: Int) {
                    navigateToChatFragment(chatRoomId)
                }
            },
            deleteListener = object : ListChatAdapter.OnChatItemDeletedListener {
                override fun onChatItemDeleted(chatRoomId: Int) {
                    showDeleteConfirmationDialog(chatRoomId)
                }
            }
        )

        binding.rvChat.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter { adapter.retry() }
        )

        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                showLoading(true)
            } else {
                showLoading(false)
                binding.swipeRefreshLayout.isRefreshing = false
            }

            val errorState = loadState.refresh as? LoadState.Error
            errorState?.let {
                showToast(it.error.localizedMessage ?: "An error occurred")
            }
        }

        binding.fabAdd.setOnClickListener {
            showCreateRoomDialog()
        }
    }

    private fun showCreateRoomDialog() {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.dialog_title, null)
        val editTextRoomTitle = view.findViewById<EditText>(R.id.titleEditText)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Create Chat Room")
            .setView(view)
            .setPositiveButton("Create", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val title = editTextRoomTitle.text.toString()
                if (title.isNotEmpty()) {
                    createRoom(title, dialog)
                } else {
                    Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    private fun createRoom(title: String, dialog: AlertDialog) {
        viewModel.createRoom(title).observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showToast("Room created: ${result.data.data.title}")
                    showLoading(false)
                    val chatRoomId = result.data.data.id
                    navigateToChatFragment(chatRoomId)
                    dialog.dismiss()
                }
                is ResultState.Error -> {
                    showToast(result.error)
                    showLoading(false)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getData()
    }

    private fun getData() {
        viewModel.chat.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(lifecycle, pagingData)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun deleteChatRoom(chatRoomId: Int) {
        viewModel.deleteChatRoom(chatRoomId).observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Loading -> showLoading(true)
                is ResultState.Success -> {
                    showToast("Room deleted successfully")
                    adapter.refresh()
                    showLoading(false)
                }
                is ResultState.Error -> {
                    showToast(result.error)
                    showLoading(false)
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog(chatRoomId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Chat Room")
            .setMessage("Are you sure you want to delete this chat room?")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteChatRoom(chatRoomId)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }


    private fun navigateToChatFragment(chatRoomId: Int) {
        val action = ChatHistoryFragmentDirections.actionChatHistoryFragmentToChatFragment(chatRoomId)
        findNavController().navigate(action)
    }
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}