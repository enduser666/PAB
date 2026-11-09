package com.azriel.inventaris059.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageHelper059 {

    private const val MAX_DIMENSION_059 = 1024
    private const val JPEG_QUALITY_059 = 85
    private const val PHOTOS_DIR_059 = "photos_059"
    private const val CAMERA_CACHE_DIR_059 = "camera_photos_059"

    fun getPhotosDir059(context: Context): File {
        val dir059 = File(context.filesDir, PHOTOS_DIR_059)
        if (!dir059.exists()) {
            dir059.mkdirs()
        }
        return dir059
    }

    fun getCameraCacheDir059(context: Context): File {
        val dir059 = File(context.cacheDir, CAMERA_CACHE_DIR_059)
        if (!dir059.exists()) {
            dir059.mkdirs()
        }
        return dir059
    }

    fun createTempCameraUri059(context: Context): Pair<Uri, String> {
        val cacheDir059 = getCameraCacheDir059(context)
        val tempFile059 = File(cacheDir059, "cam_temp_059_${System.currentTimeMillis()}.jpg")
        val uri059 = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider059",
            tempFile059
        )
        return Pair(uri059, tempFile059.absolutePath)
    }

    suspend fun saveAndCompressPhoto059(context: Context, sourceUri: Uri): String? =
        withContext(Dispatchers.IO) {
            try {
                // 1. Baca dimensi gambar terlebih dahulu tanpa load full bitmap ke memori
                val options059 = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    BitmapFactory.decodeStream(input, null, options059)
                }

                if (options059.outWidth <= 0 || options059.outHeight <= 0) {
                    return@withContext null
                }

                // 2. Hitung inSampleSize agar dimensi terpanjang <= MAX_DIMENSION_059 (1024px)
                var inSampleSize059 = 1
                val maxDim059 = maxOf(options059.outWidth, options059.outHeight)
                while ((maxDim059 / inSampleSize059) > MAX_DIMENSION_059) {
                    inSampleSize059 *= 2
                }

                val decodeOptions059 = BitmapFactory.Options().apply {
                    inSampleSize = inSampleSize059
                    inPreferredConfig = Bitmap.Config.RGB_565
                }

                val bitmap059 = context.contentResolver.openInputStream(sourceUri)?.use { input ->
                    BitmapFactory.decodeStream(input, null, decodeOptions059)
                } ?: return@withContext null

                // 3. Simpan ke filesDir/photos_059/ dengan nama unik
                val destDir059 = getPhotosDir059(context)
                val fileName059 = "photo_059_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(8)}.jpg"
                val destFile059 = File(destDir059, fileName059)

                FileOutputStream(destFile059).use { outStream ->
                    bitmap059.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY_059, outStream)
                }

                bitmap059.recycle()
                destFile059.absolutePath
            } catch (e: Exception) {
                null
            }
        }

    fun deletePhotoFile059(path059: String?) {
        if (!path059.isNullOrBlank()) {
            try {
                val file059 = File(path059)
                if (file059.exists()) {
                    file059.delete()
                }
            } catch (_: Exception) {}
        }
    }

    fun loadDownsampledBitmap059(path059: String?, targetWidth059: Int, targetHeight059: Int): Bitmap? {
        if (path059.isNullOrBlank()) return null
        val file059 = File(path059)
        if (!file059.exists()) return null

        return try {
            val options059 = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file059.absolutePath, options059)

            var sampleSize059 = 1
            if (options059.outHeight > targetHeight059 || options059.outWidth > targetWidth059) {
                val halfHeight059 = options059.outHeight / 2
                val halfWidth059 = options059.outWidth / 2
                while ((halfHeight059 / sampleSize059) >= targetHeight059 &&
                    (halfWidth059 / sampleSize059) >= targetWidth059
                ) {
                    sampleSize059 *= 2
                }
            }

            val decodeOptions059 = BitmapFactory.Options().apply {
                inSampleSize = sampleSize059
                inPreferredConfig = Bitmap.Config.RGB_565
            }
            BitmapFactory.decodeFile(file059.absolutePath, decodeOptions059)
        } catch (e: Exception) {
            null
        }
    }
}
