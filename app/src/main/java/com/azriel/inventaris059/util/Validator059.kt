package com.azriel.inventaris059.util

import android.widget.EditText

object Validator059 {

    data class FieldError059(val field: EditText, val message: String)

    fun normalizeKategori059(raw059: String?): String? {
        if (raw059.isNullOrBlank()) return null
        val trimmed059 = raw059.trim()
        if (trimmed059.isEmpty()) return null

        // Normalisasi kapitalisasi tiap kata: "baju tidur" -> "Baju Tidur"
        return trimmed059.split(Regex("\\s+"))
            .filter { it.isNotEmpty() }
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }
    }

    fun validateRegister059(
        etUsername059: EditText,
        etPassword059: EditText,
        etConfirm059: EditText
    ): List<FieldError059> {
        val errors059 = mutableListOf<FieldError059>()
        val username059 = etUsername059.text.toString().trim()
        val password059 = etPassword059.text.toString()
        val confirm059 = etConfirm059.text.toString()

        if (username059.isEmpty()) errors059 += FieldError059(etUsername059, "Username tidak boleh kosong")
        if (password059.isEmpty()) errors059 += FieldError059(etPassword059, "Password tidak boleh kosong")
        else if (password059.length < 6) errors059 += FieldError059(etPassword059, "Password minimal 6 karakter")
        if (confirm059.isEmpty()) errors059 += FieldError059(etConfirm059, "Konfirmasi password tidak boleh kosong")
        else if (password059 != confirm059) errors059 += FieldError059(etConfirm059, "Password tidak cocok")

        return errors059
    }

    fun validateLogin059(
        etUsername059: EditText,
        etPassword059: EditText
    ): List<FieldError059> {
        val errors059 = mutableListOf<FieldError059>()
        if (etUsername059.text.toString().trim().isEmpty())
            errors059 += FieldError059(etUsername059, "Username tidak boleh kosong")
        if (etPassword059.text.toString().isEmpty())
            errors059 += FieldError059(etPassword059, "Password tidak boleh kosong")
        return errors059
    }

    fun validateItemForm059(
        etNama059: EditText,
        etJumlah059: EditText,
        etHarga059: EditText,
        etDeskripsi059: EditText? = null,
        etKategori059: EditText? = null
    ): List<FieldError059> {
        val errors059 = mutableListOf<FieldError059>()
        val nama059 = etNama059.text.toString().trim()
        val jumlahStr059 = etJumlah059.text.toString().trim()
        val hargaStr059 = etHarga059.text.toString().trim()

        if (nama059.isEmpty()) {
            errors059 += FieldError059(etNama059, "Nama barang tidak boleh kosong")
        } else if (nama059.length > 100) {
            errors059 += FieldError059(etNama059, "Nama barang maksimal 100 karakter")
        }

        if (jumlahStr059.isEmpty()) {
            errors059 += FieldError059(etJumlah059, "Jumlah tidak boleh kosong")
        } else {
            val jumlah059 = jumlahStr059.toLongOrNull()
            when {
                jumlah059 == null -> errors059 += FieldError059(etJumlah059, "Jumlah harus bilangan bulat")
                jumlah059 < 0 -> errors059 += FieldError059(etJumlah059, "Jumlah tidak boleh negatif")
                jumlah059 > 1_000_000_000 -> errors059 += FieldError059(etJumlah059, "Jumlah melebihi batas wajar")
            }
        }

        if (hargaStr059.isEmpty()) {
            errors059 += FieldError059(etHarga059, "Harga tidak boleh kosong")
        } else {
            val harga059 = hargaStr059.toDoubleOrNull()
            when {
                harga059 == null -> errors059 += FieldError059(etHarga059, "Harga harus berupa angka")
                harga059 <= 0 -> errors059 += FieldError059(etHarga059, "Harga harus lebih dari 0")
                harga059 > 100_000_000_000.0 -> errors059 += FieldError059(etHarga059, "Harga melebihi batas wajar")
            }
        }

        // Validasi Deskripsi (opsional, maks 300 karakter)
        etDeskripsi059?.let { etDesk ->
            val deskripsi059 = etDesk.text.toString()
            if (deskripsi059.length > 300) {
                errors059 += FieldError059(etDesk, "Deskripsi maksimal 300 karakter")
            }
        }

        // Validasi Kategori (opsional, maks 30 karakter)
        etKategori059?.let { etKat ->
            val kategori059 = etKat.text.toString().trim()
            if (kategori059.length > 30) {
                errors059 += FieldError059(etKat, "Kategori maksimal 30 karakter")
            }
        }

        return errors059
    }

    fun validateEditProfile059(
        etUsername059: EditText,
        etPassword059: EditText,
        isPasswordChanged059: Boolean
    ): List<FieldError059> {
        val errors059 = mutableListOf<FieldError059>()
        val username059 = etUsername059.text.toString().trim()
        val password059 = etPassword059.text.toString()

        if (username059.isEmpty()) {
            errors059 += FieldError059(etUsername059, "Username tidak boleh kosong")
        }

        if (isPasswordChanged059) {
            if (password059.isEmpty()) {
                errors059 += FieldError059(etPassword059, "Password baru tidak boleh kosong")
            } else if (password059.length < 6) {
                errors059 += FieldError059(etPassword059, "Password minimal 6 karakter")
            }
        }

        return errors059
    }
}
