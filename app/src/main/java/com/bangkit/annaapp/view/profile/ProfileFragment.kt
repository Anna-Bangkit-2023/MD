package com.bangkit.annaapp.view.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bangkit.annaapp.R
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.databinding.FragmentProfileBinding
import com.bangkit.annaapp.view.ViewModelFactory
import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = context?.getString(R.string.profile)
        (activity as? AppCompatActivity)?.supportActionBar?.title = title

        binding.btnAbout.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_aboutFragment)
        }

        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.tvUsername.text = user.name
            binding.tvEmail.text = user.email
            if (user.photoProfile != null) {
                Glide.with(this).load(user.photoProfile).into(binding.ivDetailProfile)
            }
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()
        }

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
        }

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        viewModel.logoutResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultState.Success -> {
                    val message = result.data.message
                    showToast(message)
                }

                is ResultState.Error -> {
                    val errorMessage = result.error
                    showToast(errorMessage)
                    showLoading(false)
                }

                is ResultState.Loading -> {
                    showLoading(true)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            binding.tvUsername.text = user.name
            binding.tvEmail.text = user.email
            if (user.photoProfile != null) {
                Glide.with(this).load(user.photoProfile).into(binding.ivDetailProfile)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}