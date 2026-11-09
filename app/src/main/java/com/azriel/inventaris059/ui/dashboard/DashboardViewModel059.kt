package com.azriel.inventaris059.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.azriel.inventaris059.data.AppDatabase059
import com.azriel.inventaris059.data.Item059
import com.azriel.inventaris059.data.KategoriSummary059
import com.azriel.inventaris059.data.User059
import com.azriel.inventaris059.util.Constants059

class DashboardViewModel059(app: Application) : AndroidViewModel(app) {

    private val db059 = AppDatabase059.getInstance059(app)
    private val itemDao059 = db059.itemDao059()
    private val userDao059 = db059.userDao059()

    private val userId059Lv = MutableLiveData<Long>()

    // User data (reaktif)
    val user059: LiveData<User059?> = userId059Lv.switchMap { uid ->
        userDao059.observeById059(uid)
    }

    // Statistik Inti
    val jenisBarang059: LiveData<Int> = userId059Lv.switchMap { uid ->
        itemDao059.getJenisBarang059(uid)
    }

    val totalStok059: LiveData<Int?> = userId059Lv.switchMap { uid ->
        itemDao059.getTotalStok059(uid)
    }

    val stokMenipis059: LiveData<Int> = userId059Lv.switchMap { uid ->
        itemDao059.getStokMenipisCount059(uid, Constants059.LOW_STOCK_THRESHOLD_059)
    }

    val stokHabis059: LiveData<Int> = userId059Lv.switchMap { uid ->
        itemDao059.getStokHabisCount059(uid)
    }

    // Jumlah Kategori Unik
    val jumlahKategori059: LiveData<Int> = userId059Lv.switchMap { uid ->
        itemDao059.getJumlahKategori059(uid)
    }

    // Total Nilai Inventaris
    val totalNilai059: LiveData<Double?> = userId059Lv.switchMap { uid ->
        itemDao059.getTotalNilai059(uid)
    }

    // Stok Terendah (maks 3 item)
    val stokTerendah059: LiveData<List<Item059>> = userId059Lv.switchMap { uid ->
        itemDao059.getStokTerendah059(uid)
    }

    // Ringkasan Kategori (maks 4 kategori terbanyak)
    val topKategoriSummary059: LiveData<List<KategoriSummary059>> = userId059Lv.switchMap { uid ->
        itemDao059.getTopKategoriSummary059(uid)
    }

    fun init059(userId059: Long) {
        if (userId059Lv.value != userId059) {
            userId059Lv.value = userId059
        }
    }
}
