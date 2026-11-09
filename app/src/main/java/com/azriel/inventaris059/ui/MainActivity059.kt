package com.azriel.inventaris059.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.azriel.inventaris059.R
import com.azriel.inventaris059.databinding.ActivityMain059Binding
import com.azriel.inventaris059.ui.auth.LoginActivity059
import com.azriel.inventaris059.ui.dashboard.DashboardFragment059
import com.azriel.inventaris059.ui.dashboard.DashboardViewModel059
import com.azriel.inventaris059.ui.item.ItemFormFragment059
import com.azriel.inventaris059.ui.item.ItemListFragment059
import com.azriel.inventaris059.ui.profile.SettingsFragment059
import com.azriel.inventaris059.util.SessionManager059
import com.google.android.material.snackbar.Snackbar

class MainActivity059 : AppCompatActivity() {

    private lateinit var binding059: ActivityMain059Binding
    private lateinit var sessionManager059: SessionManager059
    private val dashboardViewModel059: DashboardViewModel059 by viewModels()

    private var currentTabId059: Int = R.id.nav_beranda_059

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager059 = SessionManager059(this)

        // Verifikasi sesi login
        if (!sessionManager059.isLoggedIn059()) {
            startActivity(Intent(this, LoginActivity059::class.java))
            finish()
            return
        }

        binding059 = ActivityMain059Binding.inflate(layoutInflater)
        setContentView(binding059.root)

        val userId059 = sessionManager059.getUserId059()
        dashboardViewModel059.init059(userId059)

        // Observasi user untuk inisial avatar di Top App Bar
        dashboardViewModel059.user059.observe(this) { user059 ->
            val initial059 = user059?.username059?.firstOrNull()?.uppercase() ?: "?"
            binding059.tvTopBarAvatar059.text = initial059
        }

        // Tap avatar membuka tab Pengaturan
        binding059.tvTopBarAvatar059.setOnClickListener {
            navigateToTab059(R.id.nav_pengaturan_059)
        }

        // Restore active tab saat rotasi layar
        val targetTab059 = savedInstanceState?.getInt(KEY_ACTIVE_TAB_059) ?: R.id.nav_beranda_059
        binding059.bottomNav059.selectedItemId = targetTab059
        switchTab059(targetTab059)

        // Bottom Nav Listener
        binding059.bottomNav059.setOnItemSelectedListener { item: MenuItem ->
            switchTab059(item.itemId)
            true
        }
    }

    private fun switchTab059(tabId059: Int) {
        currentTabId059 = tabId059

        val fragment059: Fragment
        val titleRes059: Int

        when (tabId059) {
            R.id.nav_tambah_059 -> {
                fragment059 = ItemFormFragment059()
                titleRes059 = R.string.nav_tambah
            }
            R.id.nav_barang_059 -> {
                fragment059 = ItemListFragment059()
                titleRes059 = R.string.nav_barang
            }
            R.id.nav_pengaturan_059 -> {
                fragment059 = SettingsFragment059()
                titleRes059 = R.string.nav_pengaturan
            }
            else -> {
                fragment059 = DashboardFragment059()
                titleRes059 = R.string.nav_beranda
            }
        }

        binding059.tvTopBarTitle059.text = getString(titleRes059)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer059, fragment059)
            .commit()
    }

    fun navigateToTab059(tabId059: Int) {
        binding059.bottomNav059.selectedItemId = tabId059
    }

    fun showSnackbar059(message059: String) {
        Snackbar.make(binding059.root, message059, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_ACTIVE_TAB_059, currentTabId059)
    }

    companion object {
        private const val KEY_ACTIVE_TAB_059 = "key_active_tab_059"
    }
}
