package com.azriel.inventaris059.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.azriel.inventaris059.data.AppDatabase059
import com.azriel.inventaris059.data.User059
import com.azriel.inventaris059.util.PasswordHasher059
import kotlinx.coroutines.launch

sealed class AuthResult059 {
    data class Success059(val userId059: Long) : AuthResult059()
    data class Error059(val message: String) : AuthResult059()
    object UsernameExists059 : AuthResult059()
    object InvalidCredentials059 : AuthResult059()
}

class AuthViewModel059(app: Application) : AndroidViewModel(app) {

    private val userDao059 = AppDatabase059.getInstance059(app).userDao059()

    fun register059(
        username: String,
        password: String,
        onResult: (AuthResult059) -> Unit
    ) {
        viewModelScope.launch {
            val trimmedUsername = username.trim()
            val exists = userDao059.countByUsername059(trimmedUsername) > 0
            if (exists) {
                onResult(AuthResult059.UsernameExists059)
                return@launch
            }
            val hashed = PasswordHasher059.hash059(password)
            val user = User059(username059 = trimmedUsername, password059 = hashed)
            val id = userDao059.insert059(user)
            onResult(AuthResult059.Success059(id))
        }
    }

    fun login059(
        username: String,
        password: String,
        onResult: (AuthResult059) -> Unit
    ) {
        viewModelScope.launch {
            val trimmedUsername = username.trim()
            val user = userDao059.findByUsername059(trimmedUsername)
            if (user == null) {
                onResult(AuthResult059.InvalidCredentials059)
                return@launch
            }
            val hashed = PasswordHasher059.hash059(password)
            if (hashed == user.password059) {
                onResult(AuthResult059.Success059(user.id059))
            } else {
                onResult(AuthResult059.InvalidCredentials059)
            }
        }
    }
}
