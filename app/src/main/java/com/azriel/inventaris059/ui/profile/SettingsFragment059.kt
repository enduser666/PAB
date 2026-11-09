package com.azriel.inventaris059.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.azriel.inventaris059.R
import com.azriel.inventaris059.databinding.FragmentSettings059Binding
import com.azriel.inventaris059.ui.auth.LoginActivity059
import com.azriel.inventaris059.util.SessionManager059
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment059 : Fragment() {

    private var _binding059: FragmentSettings059Binding? = null
    private val binding059 get() = _binding059!!

    private val viewModel059: SettingsViewModel059 by activityViewModels()
    private lateinit var sessionManager059: SessionManager059

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding059 = FragmentSettings059Binding.inflate(inflater, container, false)
        return binding059.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager059 = SessionManager059(requireContext())
        val userId059 = sessionManager059.getUserId059()
        viewModel059.init059(userId059)

        observeData059()

        // Tombol Pensil edit profil
        binding059.btnEditProfil059.setOnClickListener {
            val bottomSheet059 = EditProfileBottomSheetDialogFragment059()
            bottomSheet059.show(childFragmentManager, EditProfileBottomSheetDialogFragment059.TAG059)
        }

        // Tombol Logout
        binding059.btnLogout059.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.logout_konfirmasi_judul))
                .setMessage(getString(R.string.logout_konfirmasi_pesan))
                .setPositiveButton(getString(R.string.keluar)) { _, _ ->
                    sessionManager059.clearSession059()
                    val intent059 = Intent(requireContext(), LoginActivity059::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent059)
                    activity?.finish()
                }
                .setNegativeButton(getString(R.string.batal), null)
                .show()
        }
    }

    private fun observeData059() {
        // Data user (reaktif)
        viewModel059.user059.observe(viewLifecycleOwner) { user059 ->
            if (user059 != null) {
                binding059.tvProfilUsername059.text = user059.username059
            }
        }

        // Jumlah jenis barang (dihitung dari query)
        viewModel059.jumlahJenisBarang059.observe(viewLifecycleOwner) { count059 ->
            binding059.tvProfilJumlahBarang059.text = getString(R.string.item_count_format, count059 ?: 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding059 = null
    }
}
