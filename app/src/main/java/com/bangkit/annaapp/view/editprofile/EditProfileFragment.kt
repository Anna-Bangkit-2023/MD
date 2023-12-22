package com.bangkit.annaapp.view.editprofile

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bangkit.annaapp.R
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.databinding.FragmentEditProfileBinding
import com.bangkit.annaapp.utils.reduceFileImage
import com.bangkit.annaapp.utils.uriToFile
import com.bangkit.annaapp.view.ViewModelFactory
import com.bumptech.glide.Glide

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private var currentImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.nameEditTextLayout.hint = user.name
            if (user.photoProfile != null) {
                Glide.with(this).load(user.photoProfile).into(binding.ivDetailProfile)
            }
        }

        binding.ivClose.setOnClickListener {
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

        binding.ivDetailProfile.setOnClickListener {
            startGallery()
        }

        binding.btnSave.setOnClickListener {
            val name = binding.nameEditText.text.toString().ifBlank { null }
            val imageFile = currentImageUri?.let { uri ->
                uriToFile(uri, requireContext()).reduceFileImage()
            }

            viewModel.updateProfile(imageFile, name).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is ResultState.Loading -> {
                        showLoading(true)
                    }

                    is ResultState.Success -> {
                        showToast("Profile updated successfully")
                        showLoading(false)
                        findNavController().popBackStack()
                    }

                    is ResultState.Error -> {
                        showToast(result.error)
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            showToast("No media selected")
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            binding.ivDetailProfile.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}