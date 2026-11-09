package com.azriel.inventaris059

import com.azriel.inventaris059.util.CurrencyFormatter059
import com.azriel.inventaris059.util.ImageHelper059
import com.azriel.inventaris059.util.PasswordHasher059
import com.azriel.inventaris059.util.Validator059
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class UtilTest059 {

    @Test
    fun testPasswordHasher059() {
        val plain059 = "password123"
        val hash1059 = PasswordHasher059.hash059(plain059)
        val hash2059 = PasswordHasher059.hash059(plain059)

        assertEquals("Hash SHA-256 harus konsisten", hash1059, hash2059)
        assertNotEquals("Hash tidak boleh sama dengan plain text", plain059, hash1059)
        assertEquals("Panjang hash SHA-256 harus 64 karakter hex", 64, hash1059.length)
    }

    @Test
    fun testCurrencyFormatter059() {
        val formattedZero059 = CurrencyFormatter059.format059(0.0)
        assertEquals("Rp 0", formattedZero059)

        val formattedLarge059 = CurrencyFormatter059.format059(1250000.0)
        assertEquals("Rp 1.250.000", formattedLarge059)
    }

    @Test
    fun testKategoriNormalization059() {
        // Uji normalisasi kapitalisasi dan trim
        assertEquals("Baju", Validator059.normalizeKategori059("  baju  "))
        assertEquals("Baju", Validator059.normalizeKategori059("BAJU"))
        assertEquals("Baju Tidur", Validator059.normalizeKategori059("baju   tidur"))
        assertEquals("Sepatu Olahraga", Validator059.normalizeKategori059("sEpAtU oLaHrAgA"))

        // Null atau blank harus menghasilkan null
        assertNull(Validator059.normalizeKategori059(null))
        assertNull(Validator059.normalizeKategori059(""))
        assertNull(Validator059.normalizeKategori059("   "))
    }

    @Test
    fun testPhotoFileDeletion059() {
        // Buat file sementara untuk uji logika pembersihan file foto
        val tempFile059 = File.createTempFile("test_foto_059_", ".jpg")
        tempFile059.writeText("dummy image content")
        assertTrue("File sementara harus ada", tempFile059.exists())

        // Eksekusi fungsi pembersihan
        ImageHelper059.deletePhotoFile059(tempFile059.absolutePath)

        // Verifikasi file berhasil terhapus
        assertFalse("File foto lama harus terhapus dari disk", tempFile059.exists())

        // Memanggil deletePhotoFile059 dengan null atau path non-existent tidak boleh melempar exception
        ImageHelper059.deletePhotoFile059(null)
        ImageHelper059.deletePhotoFile059("non_existent_file_path_059.jpg")
    }
}
