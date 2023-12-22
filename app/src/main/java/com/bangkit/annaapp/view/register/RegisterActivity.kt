package com.bangkit.annaapp.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.bangkit.annaapp.data.ResultState
import com.bangkit.annaapp.databinding.ActivityRegisterBinding
import com.bangkit.annaapp.view.ViewModelFactory
import com.bangkit.annaapp.view.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private val viewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.isButtonEnabled.observe(this) { isEnabled ->
            binding.signupButton.isEnabled = isEnabled
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val name = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val phone = binding.phoneEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
                val passwordConfirm = binding.passwordConfirmEditText.text.toString()
                viewModel.checkText(name, email, phone, password, passwordConfirm)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        binding.nameEditText.addTextChangedListener(textWatcher)
        binding.emailEditText.addTextChangedListener(textWatcher)
        binding.phoneEditText.addTextChangedListener(textWatcher)
        binding.passwordEditText.addTextChangedListener(textWatcher)
        binding.passwordConfirmEditText.addTextChangedListener(textWatcher)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            register()
        }

        binding.tvLogin.setOnClickListener {
            navigateToLoginActivity()
        }
    }

    private fun register() {
        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        val passwordConfirm = binding.passwordConfirmEditText.text.toString()

        viewModel.register(name, email, phone, password, passwordConfirm)

        viewModel.registrationResult.observe(this) { result ->
            when (result) {
                is ResultState.Loading -> {
                    showLoading(true)
                }

                is ResultState.Success -> {
                    val message = result.data.message
                    showToast(message)
                    navigateToLoginActivity()
                    showLoading(false)
                }

                is ResultState.Error -> {
                    val errorMessage = result.error
                    showToast(errorMessage)
                    showLoading(false)
                }
            }
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(200)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(200)
        val name =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val phone =
            ObjectAnimator.ofFloat(binding.phoneEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(200)
        val passwordConfirm =
            ObjectAnimator.ofFloat(binding.passwordConfirmEditTextLayout, View.ALPHA, 1f)
                .setDuration(200)
        val button =
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                name,
                email,
                phone,
                password,
                passwordConfirm,
                button
            )
            startDelay = 100
        }.start()
    }
}