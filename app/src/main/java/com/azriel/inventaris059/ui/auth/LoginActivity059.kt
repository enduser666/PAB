package com.azriel.inventaris059.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.azriel.inventaris059.databinding.ActivityLogin059Binding
import com.azriel.inventaris059.ui.MainActivity059
import com.azriel.inventaris059.util.SessionManager059
import com.azriel.inventaris059.util.Validator059

class LoginActivity059 : AppCompatActivity() {

    private lateinit var binding059: ActivityLogin059Binding
    private lateinit var sessionManager059: SessionManager059
    private val viewModel059: AuthViewModel059 by viewModels()

    private var isLoading059 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager059 = SessionManager059(this)

        // Jika sesi sudah ada, langsung ke MainActivity059
        if (sessionManager059.isLoggedIn059()) {
            goToMain059()
            return
        }

        binding059 = ActivityLogin059Binding.inflate(layoutInflater)
        setContentView(binding059.root)

        binding059.btnLogin059.setOnClickListener {
            if (isLoading059) return@setOnClickListener
            handleLogin059()
        }

        binding059.tvGoRegister059.setOnClickListener {
            startActivity(Intent(this, RegisterActivity059::class.java))
        }
    }

    private fun handleLogin059() {
        clearErrors059()
        val errors059 = Validator059.validateLogin059(binding059.etUsername059, binding059.etPassword059)
        if (errors059.isNotEmpty()) {
            errors059.forEach { it.field.error = it.message }
            return
        }

        isLoading059 = true
        binding059.btnLogin059.isEnabled = false

        val username059 = binding059.etUsername059.text.toString().trim()
        val password059 = binding059.etPassword059.text.toString()

        viewModel059.login059(username059, password059) { result059 ->
            runOnUiThread {
                isLoading059 = false
                binding059.btnLogin059.isEnabled = true
                when (result059) {
                    is AuthResult059.Success059 -> {
                        sessionManager059.saveUserId059(result059.userId059)
                        goToMain059()
                    }
                    is AuthResult059.InvalidCredentials059 ->
                        binding059.etPassword059.error = "Username atau password salah"
                    else ->
                        binding059.etPassword059.error = "Terjadi kesalahan, coba lagi"
                }
            }
        }
    }

    private fun clearErrors059() {
        binding059.etUsername059.error = null
        binding059.etPassword059.error = null
    }

    private fun goToMain059() {
        val intent059 = Intent(this, MainActivity059::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent059)
        finish()
    }
}
