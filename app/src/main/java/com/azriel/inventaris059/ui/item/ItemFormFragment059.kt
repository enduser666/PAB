package com.azriel.inventaris059.ui.item

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.azriel.inventaris059.R
import com.azriel.inventaris059.databinding.FragmentItemForm059Binding
import com.azriel.inventaris059.ui.MainActivity059
import com.azriel.inventaris059.util.ImageHelper059
import com.azriel.inventaris059.util.SessionManager059
import com.azriel.inventaris059.util.Validator059
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File

class ItemFormFragment059 : Fragment() {

    private var _binding059: FragmentItemForm059Binding? = null
    private val binding059 get() = _binding059!!

    private val viewModel059: ItemViewModel059 by activityViewModels()
    private lateinit var sessionManager059: SessionManager059

    private var isSaving059 = false

    // Galeri launcher: PickVisualMedia (tanpa storage permission)
    private val pickMedia059 = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val savedPath059 = ImageHelper059.saveAndCompressPhoto059(requireContext(), uri)
                // Jika sebelumnya ada foto sementara yang baru dipilih, bersihkan
                val oldPath059 = viewModel059.formPhotoPath059.value
                if (oldPath059 != null && oldPath059 != savedPath059) {
                    ImageHelper059.deletePhotoFile059(oldPath059)
                }
                viewModel059.formPhotoPath059.value = savedPath059
            }
        }
    }

    // Kamera launcher: TakePicture via FileProvider
    private val takePicture059 = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val tempPath059 = viewModel059.tempCameraPath059
            if (tempPath059 != null) {
                lifecycleScope.launch {
                    val tempUri059 = Uri.fromFile(File(tempPath059))
                    val savedPath059 = ImageHelper059.saveAndCompressPhoto059(requireContext(), tempUri059)
                    ImageHelper059.deletePhotoFile059(tempPath059)
                    viewModel059.tempCameraPath059 = null

                    val oldPath059 = viewModel059.formPhotoPath059.value
                    if (oldPath059 != null && oldPath059 != savedPath059) {
                        ImageHelper059.deletePhotoFile059(oldPath059)
                    }
                    viewModel059.formPhotoPath059.value = savedPath059
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding059 = FragmentItemForm059Binding.inflate(inflater, container, false)
        return binding059.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager059 = SessionManager059(requireContext())
        val userId059 = sessionManager059.getUserId059()
        viewModel059.init059(userId059)

        val formBinding059 = binding059.formContent059
        formBinding059.tvJudulForm059.text = getString(R.string.judul_tambah_barang)
        formBinding059.btnHapus059.visibility = View.GONE

        // Area Foto Click
        formBinding059.cardFotoContainer059.setOnClickListener {
            showPhotoOptionsDialog059()
        }

        // Observasi path foto untuk preview
        viewModel059.formPhotoPath059.observe(viewLifecycleOwner) { photoPath059 ->
            updatePhotoPreview059(photoPath059)
        }

        // Setup saran kategori otomatis dari query Room
        viewModel059.userCategories059.observe(viewLifecycleOwner) { categories059 ->
            val adapter059 = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                categories059
            )
            formBinding059.actvKategori059.setAdapter(adapter059)
        }

        formBinding059.btnSimpan059.setOnClickListener {
            if (isSaving059) return@setOnClickListener
            handleSave059()
        }
    }

    private fun updatePhotoPreview059(photoPath059: String?) {
        val formBinding059 = binding059.formContent059
        if (!photoPath059.isNullOrBlank()) {
            val bitmap059 = ImageHelper059.loadDownsampledBitmap059(photoPath059, 320, 320)
            if (bitmap059 != null) {
                formBinding059.ivPreviewFoto059.setImageBitmap(bitmap059)
                formBinding059.ivPreviewFoto059.visibility = View.VISIBLE
                formBinding059.layoutFotoPlaceholder059.visibility = View.GONE
                return
            }
        }
        formBinding059.ivPreviewFoto059.setImageDrawable(null)
        formBinding059.ivPreviewFoto059.visibility = View.GONE
        formBinding059.layoutFotoPlaceholder059.visibility = View.VISIBLE
    }

    private fun showPhotoOptionsDialog059() {
        val hasPhoto059 = !viewModel059.formPhotoPath059.value.isNullOrBlank()
        val options059 = if (hasPhoto059) {
            arrayOf(
                getString(R.string.ambil_foto),
                getString(R.string.pilih_galeri),
                getString(R.string.hapus_foto)
            )
        } else {
            arrayOf(
                getString(R.string.ambil_foto),
                getString(R.string.pilih_galeri)
            )
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.pilih_sumber_foto))
            .setItems(options059) { _, which ->
                when (which) {
                    0 -> openCamera059()
                    1 -> openGallery059()
                    2 -> removePhoto059()
                }
            }
            .show()
    }

    private fun openCamera059() {
        try {
            val pair059 = ImageHelper059.createTempCameraUri059(requireContext())
            viewModel059.tempCameraPath059 = pair059.second
            takePicture059.launch(pair059.first)
        } catch (_: Exception) {}
    }

    private fun openGallery059() {
        pickMedia059.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun removePhoto059() {
        val currentPath059 = viewModel059.formPhotoPath059.value
        ImageHelper059.deletePhotoFile059(currentPath059)
        viewModel059.formPhotoPath059.value = null
    }

    private fun handleSave059() {
        val formBinding059 = binding059.formContent059
        clearErrors059()

        val errors059 = Validator059.validateItemForm059(
            formBinding059.etNama059,
            formBinding059.etJumlah059,
            formBinding059.etHarga059,
            formBinding059.etDeskripsi059,
            formBinding059.actvKategori059
        )

        if (errors059.isNotEmpty()) {
            errors059.forEach { it.field.error = it.message }
            return
        }

        // Cegah double-tap
        isSaving059 = true
        formBinding059.btnSimpan059.isEnabled = false

        val nama059 = formBinding059.etNama059.text.toString().trim()
        val deskripsi059 = formBinding059.etDeskripsi059.text.toString()
        val kategori059 = formBinding059.actvKategori059.text.toString()
        val jumlah059 = formBinding059.etJumlah059.text.toString().trim().toInt()
        val harga059 = formBinding059.etHarga059.text.toString().trim().toDouble()
        val fotoPath059 = viewModel059.formPhotoPath059.value
        val userId059 = sessionManager059.getUserId059()

        viewModel059.saveItem059(
            itemId059 = null,
            userId059 = userId059,
            nama059 = nama059,
            jumlah059 = jumlah059,
            harga059 = harga059,
            fotoPath059 = fotoPath059,
            deskripsi059 = deskripsi059,
            kategori059 = kategori059
        ) {
            activity?.runOnUiThread {
                isSaving059 = false
                formBinding059.btnSimpan059.isEnabled = true

                // Kosongkan form
                formBinding059.etNama059.setText("")
                formBinding059.etDeskripsi059.setText("")
                formBinding059.actvKategori059.setText("")
                formBinding059.etJumlah059.setText("")
                formBinding059.etHarga059.setText("")
                viewModel059.clearFormState059()
                clearErrors059()

                // Navigasi ke tab Barang dan tampilkan Snackbar
                (activity as? MainActivity059)?.apply {
                    navigateToTab059(R.id.nav_barang_059)
                    showSnackbar059(getString(R.string.msg_barang_ditambahkan))
                }
            }
        }
    }

    private fun clearErrors059() {
        val formBinding059 = binding059.formContent059
        formBinding059.etNama059.error = null
        formBinding059.etDeskripsi059.error = null
        formBinding059.actvKategori059.error = null
        formBinding059.etJumlah059.error = null
        formBinding059.etHarga059.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding059 = null
    }
}
