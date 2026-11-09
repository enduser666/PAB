package com.azriel.inventaris059.ui.item

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.azriel.inventaris059.R
import com.azriel.inventaris059.databinding.ActivityItemForm059Binding
import com.azriel.inventaris059.util.ImageHelper059
import com.azriel.inventaris059.util.SessionManager059
import com.azriel.inventaris059.util.Validator059
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.io.File

class ItemFormActivity059 : AppCompatActivity() {

    private lateinit var binding059: ActivityItemForm059Binding
    private lateinit var sessionManager059: SessionManager059
    private val viewModel059: ItemViewModel059 by viewModels()

    private var itemId059: Long? = null
    private var isSaving059 = false

    private val pickMedia059 = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val savedPath059 = ImageHelper059.saveAndCompressPhoto059(this@ItemFormActivity059, uri)
                val oldPath059 = viewModel059.formPhotoPath059.value
                if (oldPath059 != null && oldPath059 != savedPath059) {
                    ImageHelper059.deletePhotoFile059(oldPath059)
                }
                viewModel059.formPhotoPath059.value = savedPath059
            }
        }
    }

    private val takePicture059 = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val tempPath059 = viewModel059.tempCameraPath059
            if (tempPath059 != null) {
                lifecycleScope.launch {
                    val tempUri059 = Uri.fromFile(File(tempPath059))
                    val savedPath059 = ImageHelper059.saveAndCompressPhoto059(this@ItemFormActivity059, tempUri059)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding059 = ActivityItemForm059Binding.inflate(layoutInflater)
        setContentView(binding059.root)

        sessionManager059 = SessionManager059(this)
        val userId059 = sessionManager059.getUserId059()
        viewModel059.init059(userId059)

        setSupportActionBar(binding059.toolbar059)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        itemId059 = intent.getLongExtra(EXTRA_ITEM_ID_059, -1L).takeIf { it != -1L }

        val formBinding059 = binding059.formContent059

        if (itemId059 != null) {
            // Mode Edit / Ubah Barang
            binding059.tvToolbarTitle059.text = getString(R.string.judul_edit_barang)
            formBinding059.tvJudulForm059.visibility = View.GONE
            formBinding059.btnHapus059.visibility = View.VISIBLE

            viewModel059.loadItem059(itemId059!!, userId059)
            observeCurrentItem059()
        } else {
            // Mode Tambah
            binding059.tvToolbarTitle059.text = getString(R.string.judul_tambah_barang)
            formBinding059.tvJudulForm059.visibility = View.GONE
            formBinding059.btnHapus059.visibility = View.GONE
        }

        // Area Foto Click
        formBinding059.cardFotoContainer059.setOnClickListener {
            showPhotoOptionsDialog059()
        }

        // Observasi path foto untuk preview
        viewModel059.formPhotoPath059.observe(this) { photoPath059 ->
            updatePhotoPreview059(photoPath059)
        }

        // Setup saran kategori otomatis dari query Room
        viewModel059.userCategories059.observe(this) { categories059 ->
            val adapter059 = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                categories059
            )
            formBinding059.actvKategori059.setAdapter(adapter059)
        }

        formBinding059.btnSimpan059.setOnClickListener {
            if (isSaving059) return@setOnClickListener
            handleSave059()
        }

        formBinding059.btnHapus059.setOnClickListener {
            showDeleteConfirmDialog059()
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

        MaterialAlertDialogBuilder(this)
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
            val pair059 = ImageHelper059.createTempCameraUri059(this)
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

    private fun observeCurrentItem059() {
        val formBinding059 = binding059.formContent059
        viewModel059.currentItem059.observe(this) { item059 ->
            item059 ?: return@observe
            formBinding059.etNama059.setText(item059.nama059)
            formBinding059.etDeskripsi059.setText(item059.deskripsi059 ?: "")
            formBinding059.actvKategori059.setText(item059.kategori059 ?: "")
            formBinding059.etJumlah059.setText(item059.jumlah059.toString())
            formBinding059.etHarga059.setText(item059.harga059.toBigDecimal().stripTrailingZeros().toPlainString())
            if (viewModel059.formPhotoPath059.value == null) {
                viewModel059.formPhotoPath059.value = item059.fotoPath059
            }
        }
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
            itemId059 = itemId059,
            userId059 = userId059,
            nama059 = nama059,
            jumlah059 = jumlah059,
            harga059 = harga059,
            fotoPath059 = fotoPath059,
            deskripsi059 = deskripsi059,
            kategori059 = kategori059
        ) {
            runOnUiThread {
                isSaving059 = false
                finish()
            }
        }
    }

    private fun showDeleteConfirmDialog059() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.hapus_konfirmasi_judul))
            .setMessage(getString(R.string.hapus_konfirmasi_pesan))
            .setPositiveButton(getString(R.string.hapus)) { _, _ ->
                val userId059 = sessionManager059.getUserId059()
                itemId059?.let { id059 ->
                    viewModel059.deleteItem059(id059, userId059) {
                        runOnUiThread { finish() }
                    }
                }
            }
            .setNegativeButton(getString(R.string.batal), null)
            .show()
    }

    private fun clearErrors059() {
        val formBinding059 = binding059.formContent059
        formBinding059.etNama059.error = null
        formBinding059.etDeskripsi059.error = null
        formBinding059.actvKategori059.error = null
        formBinding059.etJumlah059.error = null
        formBinding059.etHarga059.error = null
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_ITEM_ID_059 = "extra_item_id_059"
    }
}
