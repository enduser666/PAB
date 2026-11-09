package com.azriel.inventaris059.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.azriel.inventaris059.databinding.BottomSheetEditProfile059Binding
import com.azriel.inventaris059.util.SessionManager059
import com.azriel.inventaris059.util.Validator059
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditProfileBottomSheetDialogFragment059 : BottomSheetDialogFragment() {

    private var _binding059: BottomSheetEditProfile059Binding? = null
    private val binding059 get() = _binding059!!

    private val viewModel059: SettingsViewModel059 by activityViewModels()
    private lateinit var sessionManager059: SessionManager059

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding059 = BottomSheetEditProfile059Binding.inflate(inflater, container, false)
        return binding059.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager059 = SessionManager059(requireContext())
        val userId059 = sessionManager059.getUserId059()

        // Isi default username dari data user saat ini
        viewModel059.user059.value?.let { user059 ->
            binding059.etEditUsername059.setText(user059.username059)
        }

        binding059.btnSimpanProfil059.setOnClickListener {
            handleSave059(userId059)
        }
    }

    private fun handleSave059(userId059: Long) {
        clearErrors059()

        val passwordText059 = binding059.etEditPassword059.text.toString()
        val isPasswordChanged059 = passwordText059.isNotEmpty()

        val errors059 = Validator059.validateEditProfile059(
            binding059.etEditUsername059,
            binding059.etEditPassword059,
            isPasswordChanged059
        )

        if (errors059.isNotEmpty()) {
            errors059.forEach { it.field.error = it.message }
            return
        }

        val newUsername059 = binding059.etEditUsername059.text.toString().trim()
        val newPassword059 = if (isPasswordChanged059) passwordText059 else null

        binding059.btnSimpanProfil059.isEnabled = false

        viewModel059.updateProfile059(userId059, newUsername059, newPassword059) { result059 ->
            activity?.runOnUiThread {
                binding059.btnSimpanProfil059.isEnabled = true
                when (result059) {
                    is ProfileUpdateResult059.Success059 -> {
                        dismiss()
                    }
                    is ProfileUpdateResult059.UsernameExists059 -> {
                        binding059.etEditUsername059.error = "Username sudah digunakan user lain"
                    }
                    is ProfileUpdateResult059.Error059 -> {
                        binding059.etEditUsername059.error = result059.message059
                    }
                }
            }
        }
    }

    private fun clearErrors059() {
        binding059.etEditUsername059.error = null
        binding059.etEditPassword059.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding059 = null
    }

    companion object {
        const val TAG059 = "EditProfileBottomSheet059"
    }
}
