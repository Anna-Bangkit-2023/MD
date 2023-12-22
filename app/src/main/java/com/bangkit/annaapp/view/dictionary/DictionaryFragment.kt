package com.bangkit.annaapp.view.dictionary

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.annaapp.R
import com.bangkit.annaapp.data.remote.response.DictionaryResponse
import com.bangkit.annaapp.databinding.FragmentDictionaryBinding
import com.bangkit.annaapp.view.ViewModelFactory
import com.bangkit.annaapp.view.adapter.DescriptionAdapter
import java.io.IOException

class DictionaryFragment : Fragment() {

    private var _binding: FragmentDictionaryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DictionaryViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var descriptionAdapter: DescriptionAdapter

    private var mediaPlayer: MediaPlayer? = null
    private var audioUrl: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDictionaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = context?.getString(R.string.dictionary)
        (activity as? AppCompatActivity)?.supportActionBar?.title = title

        binding.detailLayout.visibility = View.GONE

        setupRecyclerView()

        binding.searchEditTextLayout.setEndIconOnClickListener {
            searchWord()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchWord()
                true
            } else {
                false
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner, ::showLoading)
        viewModel.errorMessage.observe(viewLifecycleOwner, ::showToast)
        viewModel.wordDetails.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                showWordDetails(response)
                descriptionAdapter =
                    DescriptionAdapter(response.meanings.flatMap { it.definitions })
                binding.rvDescription.adapter = descriptionAdapter
            } else {
                showNoDataFound()
            }
        }

        binding.btnSpeaker.setOnClickListener {
            audioUrl?.let { url ->
                playAudio(url)
            }
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.isNullOrEmpty()) {
                    binding.detailLayout.visibility = View.GONE
                }
            }
        })
    }

    private fun searchWord() {
        val word = binding.searchEditText.text.toString()
        if (word.isNotBlank()) {
            viewModel.searchWord(word)
            hideKeyboard()
        }
    }

    private fun playAudio(url: String) {
        if (url.isBlank()) {
            showToast("URL Audio tidak tersedia")
            return
        }

        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            try {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { start() }
            } catch (e: IOException) {
                showToast("Error: Tidak bisa memutar audio")
            }
            setOnErrorListener { _, _, _ ->
                showToast("Error playing audio")
                true
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvDescription.layoutManager = LinearLayoutManager(context)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showWordDetails(response: DictionaryResponse) {
        Log.d("DictionaryFragment", "Showing details for word: ${response.word}")
        with(binding) {
            progressBar.visibility = View.GONE
            detailLayout.visibility = View.VISIBLE

            tvWord.text = response.word
            tvPhonetic.text = response.phonetic
        }
        audioUrl = response.phonetics.firstOrNull()?.audio
    }

    private fun showNoDataFound() {
        showToast(getString(R.string.no_definition_available))
        with(binding) {
            detailLayout.visibility = View.GONE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}