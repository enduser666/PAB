package com.azriel.inventaris059.ui.item

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.azriel.inventaris059.data.AppDatabase059
import com.azriel.inventaris059.data.Item059
import com.azriel.inventaris059.util.Constants059
import com.azriel.inventaris059.util.ImageHelper059
import com.azriel.inventaris059.util.Validator059
import kotlinx.coroutines.launch

enum class StockFilterOption059 {
    SEMUA, TERSEDIA, MENIPIS, HABIS
}

enum class SortOption059 {
    NAMA_ASC, STOK_ASC, HARGA_DESC
}

class ItemViewModel059(app: Application) : AndroidViewModel(app) {

    private val itemDao059 = AppDatabase059.getInstance059(app).itemDao059()

    private val userId059Lv = MutableLiveData<Long>()

    // Sumber data item dari Room (reaktif)
    val rawItems059: LiveData<List<Item059>> = userId059Lv.switchMap { uid ->
        itemDao059.getAllByUser059(uid)
    }

    // Daftar kategori DISTINCT milik user dari Room
    val userCategories059: LiveData<List<String>> = userId059Lv.switchMap { uid ->
        itemDao059.getDistinctKategori059(uid)
    }

    // Filter & Sort state
    val searchQuery059 = MutableLiveData<String>("")
    val filterStock059 = MutableLiveData<StockFilterOption059>(StockFilterOption059.SEMUA)
    val filterCategory059 = MutableLiveData<String?>(null) // null = Semua
    val sortOption059 = MutableLiveData<SortOption059>(SortOption059.NAMA_ASC)

    // State foto pada form (aman saat rotasi layar)
    val formPhotoPath059 = MutableLiveData<String?>(null)
    var tempCameraPath059: String? = null

    // LiveData gabungan setelah difilter dan diurutkan
    val filteredItems059 = MediatorLiveData<List<Item059>>().apply {
        val updateBlock059 = {
            val list059 = rawItems059.value ?: emptyList()
            val query059 = searchQuery059.value.orEmpty().trim().lowercase()
            val stockFilter059 = filterStock059.value ?: StockFilterOption059.SEMUA
            val categoryFilter059 = filterCategory059.value
            val sort059 = sortOption059.value ?: SortOption059.NAMA_ASC

            // 1. Filter nama DAN kategori (query search)
            var result059 = if (query059.isEmpty()) {
                list059
            } else {
                list059.filter { item059 ->
                    item059.nama059.lowercase().contains(query059) ||
                            (item059.kategori059?.lowercase()?.contains(query059) == true)
                }
            }

            // 2. Filter kategori spesifik jika dipilih
            if (!categoryFilter059.isNullOrBlank()) {
                result059 = result059.filter {
                    it.kategori059.equals(categoryFilter059, ignoreCase = true)
                }
            }

            // 3. Filter status stok
            result059 = when (stockFilter059) {
                StockFilterOption059.SEMUA -> result059
                StockFilterOption059.TERSEDIA -> result059.filter { it.jumlah059 > 0 }
                StockFilterOption059.MENIPIS -> result059.filter {
                    it.jumlah059 > 0 && it.jumlah059 <= Constants059.LOW_STOCK_THRESHOLD_059
                }
                StockFilterOption059.HABIS -> result059.filter { it.jumlah059 == 0 }
            }

            // 4. Urutkan (Sort)
            result059 = when (sort059) {
                SortOption059.NAMA_ASC -> result059.sortedBy { it.nama059.lowercase() }
                SortOption059.STOK_ASC -> result059.sortedWith(compareBy({ it.jumlah059 }, { it.nama059.lowercase() }))
                SortOption059.HARGA_DESC -> result059.sortedWith(compareByDescending<Item059> { it.harga059 }.thenBy { it.nama059.lowercase() })
            }

            value = result059
        }

        addSource(rawItems059) { updateBlock059() }
        addSource(searchQuery059) { updateBlock059() }
        addSource(filterStock059) { updateBlock059() }
        addSource(filterCategory059) { updateBlock059() }
        addSource(sortOption059) { updateBlock059() }
    }

    private val _currentItem059 = MutableLiveData<Item059?>()
    val currentItem059: LiveData<Item059?> get() = _currentItem059

    fun init059(userId059: Long) {
        if (userId059Lv.value != userId059) {
            userId059Lv.value = userId059
        }
    }

    fun getUserId059(): Long = userId059Lv.value ?: -1L

    fun isFilterActive059(): Boolean {
        val stock059 = filterStock059.value ?: StockFilterOption059.SEMUA
        val sort059 = sortOption059.value ?: SortOption059.NAMA_ASC
        val cat059 = filterCategory059.value
        return stock059 != StockFilterOption059.SEMUA ||
                sort059 != SortOption059.NAMA_ASC ||
                !cat059.isNullOrBlank()
    }

    fun resetFilter059() {
        filterStock059.value = StockFilterOption059.SEMUA
        sortOption059.value = SortOption059.NAMA_ASC
        filterCategory059.value = null
    }

    fun setCategoryFilter059(category059: String?) {
        filterCategory059.value = category059
    }

    fun loadItem059(itemId059: Long, userId059: Long) {
        viewModelScope.launch {
            val item059 = itemDao059.getItemByIdAndUser059(itemId059, userId059)
            _currentItem059.postValue(item059)
            if (formPhotoPath059.value == null) {
                formPhotoPath059.postValue(item059?.fotoPath059)
            }
        }
    }

    fun saveItem059(
        itemId059: Long?,
        userId059: Long,
        nama059: String,
        jumlah059: Int,
        harga059: Double,
        fotoPath059: String?,
        deskripsi059: String?,
        kategori059: String?,
        onResult059: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val normalizedKategori059 = Validator059.normalizeKategori059(kategori059)
            val trimmedDeskripsi059 = deskripsi059?.trim().takeIf { !it.isNullOrEmpty() }

            if (itemId059 == null || itemId059 == -1L) {
                val item059 = Item059(
                    userId059 = userId059,
                    nama059 = nama059.trim(),
                    jumlah059 = jumlah059,
                    harga059 = harga059,
                    fotoPath059 = fotoPath059,
                    deskripsi059 = trimmedDeskripsi059,
                    kategori059 = normalizedKategori059
                )
                itemDao059.insert059(item059)
            } else {
                val existing059 = itemDao059.getItemByIdAndUser059(itemId059, userId059)
                if (existing059 != null) {
                    // Jika foto diganti atau dihapus, hapus file foto lama dari disk
                    if (existing059.fotoPath059 != null && existing059.fotoPath059 != fotoPath059) {
                        ImageHelper059.deletePhotoFile059(existing059.fotoPath059)
                    }

                    itemDao059.update059(
                        existing059.copy(
                            nama059 = nama059.trim(),
                            jumlah059 = jumlah059,
                            harga059 = harga059,
                            fotoPath059 = fotoPath059,
                            deskripsi059 = trimmedDeskripsi059,
                            kategori059 = normalizedKategori059
                        )
                    )
                }
            }
            formPhotoPath059.postValue(null)
            tempCameraPath059 = null
            onResult059(true)
        }
    }

    fun deleteItem059(itemId059: Long, userId059: Long, onResult059: () -> Unit) {
        viewModelScope.launch {
            val item059 = itemDao059.getItemByIdAndUser059(itemId059, userId059)
            // Hapus file foto dari disk sebelum menghapus data dari DB
            item059?.fotoPath059?.let { path059 ->
                ImageHelper059.deletePhotoFile059(path059)
            }
            itemDao059.delete059(itemId059, userId059)
            onResult059()
        }
    }

    fun clearFormState059() {
        // Hapus file sementara kamera jika ada
        tempCameraPath059?.let { ImageHelper059.deletePhotoFile059(it) }
        tempCameraPath059 = null
        formPhotoPath059.value = null
        _currentItem059.value = null
    }
}
