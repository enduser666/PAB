package com.azriel.inventaris059.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.azriel.inventaris059.databinding.ActivityRegister059Binding
import com.azriel.inventaris059.ui.MainActivity059
import com.azriel.inventaris059.util.SessionManager059
import com.azriel.inventaris059.util.Validator059

class RegisterActivity059 : AppCompatActivity() {

    private lateinit var binding059: ActivityRegister059Binding
    private lateinit var sessionManager059: SessionManager059
    private val viewModel059: AuthViewModel059 by viewModels()

    private var isLoading059 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding059 = ActivityRegister059Binding.inflate(layoutInflater)
        setContentView(binding059.root)
        sessionManager059 = SessionManager059(this)

        binding059.btnRegister059.setOnClickListener {
            if (isLoading059) return@setOnClickListener
            handleRegister059()
        }

        binding059.tvGoLogin059.setOnClickListener { finish() }
    }

    private fun handleRegister059() {
        clearErrors059()
        val errors059 = Validator059.validateRegister059(
            binding059.etUsername059,
            binding059.etPassword059,
            binding059.etConfirm059
        )
        if (errors059.isNotEmpty()) {
            errors059.forEach { it.field.error = it.message }
            return
        }

        isLoading059 = true
        binding059.btnRegister059.isEnabled = false

        val username059 = binding059.etUsername059.text.toString().trim()
        val password059 = binding059.etPassword059.text.toString()

        viewModel059.register059(username059, password059) { result059 ->
            runOnUiThread {
                isLoading059 = false
                binding059.btnRegister059.isEnabled = true
                when (result059) {
                    is AuthResult059.Success059 -> {
                        sessionManager059.saveUserId059(result059.userId059)
                        startActivity(
                            Intent(this, MainActivity059::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        )
                        finish()
                    }
                    is AuthResult059.UsernameExists059 ->
                        binding059.etUsername059.error = "Username sudah digunakan"
                    else ->
                        binding059.etUsername059.error = "Terjadi kesalahan, coba lagi"
                }
            }
        }
    }

    private fun clearErrors059() {
        binding059.etUsername059.error = null
        binding059.etPassword059.error = null
        binding059.etConfirm059.error = null
    }
}
