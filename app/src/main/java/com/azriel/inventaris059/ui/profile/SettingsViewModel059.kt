package com.azriel.inventaris059.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.azriel.inventaris059.data.AppDatabase059
import com.azriel.inventaris059.data.User059
import com.azriel.inventaris059.util.PasswordHasher059
import kotlinx.coroutines.launch

sealed class ProfileUpdateResult059 {
    object Success059 : ProfileUpdateResult059()
    object UsernameExists059 : ProfileUpdateResult059()
    data class Error059(val message059: String) : ProfileUpdateResult059()
}

class SettingsViewModel059(app: Application) : AndroidViewModel(app) {

    private val db059 = AppDatabase059.getInstance059(app)
    private val userDao059 = db059.userDao059()
    private val itemDao059 = db059.itemDao059()

    private val userId059Lv = MutableLiveData<Long>()

    val user059: LiveData<User059?> = userId059Lv.switchMap { uid ->
        userDao059.observeById059(uid)
    }

    val jumlahJenisBarang059: LiveData<Int> = userId059Lv.switchMap { uid ->
        itemDao059.getJenisBarang059(uid)
    }

    fun init059(userId059: Long) {
        if (userId059Lv.value != userId059) {
            userId059Lv.value = userId059
        }
    }

    fun updateProfile059(
        userId059: Long,
        newUsername059: String,
        newPassword059: String?,
        onResult059: (ProfileUpdateResult059) -> Unit
    ) {
        viewModelScope.launch {
            val trimmedUsername059 = newUsername059.trim()

            // Cek apakah username sudah dipakai oleh user LAIN
            val exists059 = userDao059.countByUsernameForOtherUser059(trimmedUsername059, userId059) > 0
            if (exists059) {
                onResult059(ProfileUpdateResult059.UsernameExists059)
                return@launch
            }

            val currentUser059 = userDao059.findById059(userId059)
            if (currentUser059 == null) {
                onResult059(ProfileUpdateResult059.Error059("User tidak ditemukan"))
                return@launch
            }

            val passwordToSave059 = if (!newPassword059.isNullOrBlank()) {
                PasswordHasher059.hash059(newPassword059)
            } else {
                currentUser059.password059
            }

            val updatedUser059 = currentUser059.copy(
                username059 = trimmedUsername059,
                password059 = passwordToSave059
            )
            userDao059.update059(updatedUser059)
            onResult059(ProfileUpdateResult059.Success059)
        }
    }
}
