package com.bangkit.annaapp.view.chat

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.annaapp.R
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.databinding.FragmentChatBinding
import com.bangkit.annaapp.view.ViewModelFactory
import com.bangkit.annaapp.view.adapter.ChatAdapter
import java.io.File
import android.Manifest
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.bangkit.annaapp.data.remote.response.MessagesItem
import com.bangkit.annaapp.utils.startRecording
import com.bangkit.annaapp.utils.stopRecording
import java.io.IOException

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: ChatAdapter
    private var chatRoomId: Int? = null
    private var currentUserToken: String? = null
    private var currentUserId: Int? = null
    private var isRecording = false
    private var recordedAudioFile: File? = null
    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingAudioIndex: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChatAdapter(0) { audioFileName, position ->
            playOrPauseAudio(audioFileName, position)
        }

        chatRoomId = arguments?.let { ChatFragmentArgs.fromBundle(it).chatRoomId }
        chatRoomId?.let {
            viewModel.getChatRoomDetails(it)
            observeChatRoomDetails()
            observeCurrentUser()
        }
        setupMessageEditTextListener()
    }

    private fun observeChatRoomDetails() {
        viewModel.chatRoomDetails.observe(viewLifecycleOwner) { roomData ->
            (activity as? AppCompatActivity)?.supportActionBar?.title = roomData.title
            adapter.setMessages(roomData.messages)
        }
    }

    private fun observeCurrentUser() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            setupRecyclerView(user.id)
            currentUserId = user.id
            currentUserToken = user.accessToken
            Log.d("token user current :", currentUserToken.toString())
        }
    }

    private fun setupRecyclerView(currentUserId: Int) {
        adapter = ChatAdapter(currentUserId) { audioFileName, position ->
            playOrPauseAudio(audioFileName, position)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupMessageEditTextListener() {
        binding.messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.messageTextInputLayout.endIconDrawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_mic)
                } else {
                    binding.messageTextInputLayout.endIconDrawable =
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_send)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        binding.messageTextInputLayout.setEndIconOnClickListener {
            val message = binding.messageEditText.text.toString()
            when {
                message.isNotEmpty() -> {
                    sendMessage(message)
                    binding.messageEditText.text?.clear()
                }

                isRecording -> {
                    handleRecording()
                }

                else -> {
                    handleRecording()
                }
            }
        }

    }

    private fun sendMessage(message: String) {
        chatRoomId?.let { roomId ->
            viewModel.sendMessage(roomId, message, null).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ResultState.Loading -> {
                    }

                    is ResultState.Success -> {
                        viewModel.reloadChatRoom(roomId)
                        binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
                    }

                    is ResultState.Error -> {
                        Toast.makeText(context, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_chat, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_editTitle -> {
                chatRoomId?.let { showEditTitleDialog(it) }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showEditTitleDialog(chatRoomId: Int) {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.dialog_title, null)
        val editTextRoomTitle = view.findViewById<EditText>(R.id.titleEditText)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Title")
            .setView(view)
            .setPositiveButton("Edit", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val newTitle = editTextRoomTitle.text.toString()
                if (newTitle.isNotEmpty()) {
                    viewModel.updateChatTitle(chatRoomId, newTitle)
                        .observe(viewLifecycleOwner) { result ->
                            when (result) {
                                is ResultState.Success -> {
                                    (activity as? AppCompatActivity)?.supportActionBar?.title =
                                        newTitle
                                    dialog.dismiss()
                                }

                                is ResultState.Error -> {
                                    Toast.makeText(
                                        context,
                                        "Error: ${result.error}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                // Handle loading state if necessary
                                else -> {}
                            }
                        }
                } else {
                    Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showSendAudioDialog(audioFile: File) {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Send Recording")
                .setMessage("Do you want to send this recording?")
                .setPositiveButton("Send") { _, _ ->
                    val tempMessage = createTempMessage(null, audioFile)
                    adapter.addMessage(tempMessage)
                    binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
                    chatRoomId?.let { roomId ->
                        viewModel.sendMessage(roomId, null, audioFile)
                            .observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is ResultState.Loading -> {
                                    }

                                    is ResultState.Success -> {
                                        viewModel.reloadChatRoom(roomId)
                                        binding.recyclerView.scrollToPosition(adapter.itemCount - 1)
                                    }

                                    is ResultState.Error -> {
                                        Toast.makeText(
                                            context,
                                            "Error: ${result.error}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    else -> {}
                                }
                            }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun requestMicPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_RECORD_AUDIO_PERMISSION
        )
    }

    private fun handleRecording() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestMicPermission()
        } else {
            if (isRecording) {
                stopRecording()
                recordedAudioFile?.let { showSendAudioDialog(it) }
                isRecording = false
                updateRecordingIcon()
            } else {
                recordedAudioFile = startRecording(requireContext())
                isRecording = true
                updateRecordingIcon()
            }
        }
    }

    private fun updateRecordingIcon() {
        val icon = if (isRecording) R.drawable.ic_stop else R.drawable.ic_mic
        binding.messageTextInputLayout.endIconDrawable =
            ContextCompat.getDrawable(requireContext(), icon)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_RECORD_AUDIO_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handleRecording()
                } else {
                    Toast.makeText(
                        context,
                        "Permission for audio recording denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun playOrPauseAudio(audioFileName: String, position: Int) {
        val tempAudioFile = File(context?.externalCacheDir, audioFileName)
        val audioSource: Uri = if (tempAudioFile.exists()) {
            Uri.fromFile(tempAudioFile)
        } else {
            Log.d("chatfragment", audioFileName)
            Uri.parse("https://storage.googleapis.com/anna_app_bucket/audio/$audioFileName")
        }

        if (currentlyPlayingAudioIndex == position && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            adapter.updatePlayPauseIcon(position, false)
        } else {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                try {
                    setDataSource(requireContext(), audioSource)
                    prepareAsync()
                    setOnPreparedListener {
                        start()
                        currentlyPlayingAudioIndex = position
                        adapter.updatePlayPauseIcon(position, true)
                    }
                } catch (e: IOException) {
                    Log.e("ChatFragment", "Error setting data source", e)
                }
                setOnCompletionListener {
                    currentlyPlayingAudioIndex = null
                    adapter.updatePlayPauseIcon(position, false)
                }
                setOnErrorListener { _, what, extra ->
                    Log.e("MediaPlayerError", "Error occurred: What: $what, Extra: $extra")
                    true
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

    private fun createTempMessage(text: String?, audioFile: File?): MessagesItem {
        val fileValue = if (audioFile != null) "audio/" + audioFile.name else text ?: ""
        return MessagesItem(
            roomChatId = chatRoomId ?: 0,
            file = fileValue,
            updatedAt = "",
            receiverId = 0,
            createdAt = "",
            id = 0,
            message = text ?: "",
            senderId = currentUserId ?: 0
        )
    }
}