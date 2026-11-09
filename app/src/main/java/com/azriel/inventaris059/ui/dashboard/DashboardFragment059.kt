package com.azriel.inventaris059.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.azriel.inventaris059.R
import com.azriel.inventaris059.data.Item059
import com.azriel.inventaris059.data.KategoriSummary059
import com.azriel.inventaris059.databinding.FragmentDashboard059Binding
import com.azriel.inventaris059.databinding.ItemKategoriSummaryRow059Binding
import com.azriel.inventaris059.databinding.ItemStokTerendahRow059Binding
import com.azriel.inventaris059.ui.MainActivity059
import com.azriel.inventaris059.ui.item.ItemFormActivity059
import com.azriel.inventaris059.ui.item.ItemViewModel059
import com.azriel.inventaris059.util.CurrencyFormatter059
import com.azriel.inventaris059.util.SessionManager059

class DashboardFragment059 : Fragment() {

    private var _binding059: FragmentDashboard059Binding? = null
    private val binding059 get() = _binding059!!

    private val dashboardViewModel059: DashboardViewModel059 by activityViewModels()
    private val itemViewModel059: ItemViewModel059 by activityViewModels()
    private lateinit var sessionManager059: SessionManager059

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding059 = FragmentDashboard059Binding.inflate(inflater, container, false)
        return binding059.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager059 = SessionManager059(requireContext())
        val userId059 = sessionManager059.getUserId059()
        dashboardViewModel059.init059(userId059)
        itemViewModel059.init059(userId059)

        observeData059()

        // Tombol Tambah Barang saat empty state stok terendah
        binding059.btnEmptyKeTambah059.setOnClickListener {
            (activity as? MainActivity059)?.navigateToTab059(R.id.nav_tambah_059)
        }
    }

    private fun observeData059() {
        // Sapaan nama user (reaktif)
        dashboardViewModel059.user059.observe(viewLifecycleOwner) { user059 ->
            if (user059 != null) {
                binding059.tvSapaan059.text = getString(R.string.sapaan_format, user059.username059)
            }
        }

        // Statistik: Jenis Barang
        dashboardViewModel059.jenisBarang059.observe(viewLifecycleOwner) { count059 ->
            binding059.tvJenisBarang059.text = (count059 ?: 0).toString()
        }

        // Statistik: Total Stok
        dashboardViewModel059.totalStok059.observe(viewLifecycleOwner) { stok059 ->
            binding059.tvTotalStok059.text = (stok059 ?: 0).toString()
        }

        // Statistik: Jumlah Kategori Unik
        dashboardViewModel059.jumlahKategori059.observe(viewLifecycleOwner) { katCount059 ->
            binding059.tvKategoriStat059.text = (katCount059 ?: 0).toString()
        }

        // Statistik: Stok Menipis
        dashboardViewModel059.stokMenipis059.observe(viewLifecycleOwner) { menipis059 ->
            binding059.tvStokMenipis059.text = (menipis059 ?: 0).toString()
        }

        // Statistik: Stok Habis
        dashboardViewModel059.stokHabis059.observe(viewLifecycleOwner) { habis059 ->
            binding059.tvStokHabis059.text = (habis059 ?: 0).toString()
        }

        // Total Nilai Inventaris (SUM null -> Rp 0)
        dashboardViewModel059.totalNilai059.observe(viewLifecycleOwner) { nilai059 ->
            binding059.tvTotalNilai059.text = CurrencyFormatter059.format059(nilai059 ?: 0.0)
        }

        // 3 Barang dengan Stok Terendah
        dashboardViewModel059.stokTerendah059.observe(viewLifecycleOwner) { items059 ->
            renderStokTerendah059(items059)
        }

        // Ringkasan Kategori (maksimal 4 kategori terbanyak)
        dashboardViewModel059.topKategoriSummary059.observe(viewLifecycleOwner) { summaryList059 ->
            renderKategoriSummary059(summaryList059)
        }
    }

    private fun renderStokTerendah059(items059: List<Item059>) {
        val listContainer059 = binding059.layoutStokTerendahList059
        listContainer059.removeAllViews()

        if (items059.isEmpty()) {
            binding059.layoutStokTerendahEmpty059.visibility = View.VISIBLE
            listContainer059.visibility = View.GONE
        } else {
            binding059.layoutStokTerendahEmpty059.visibility = View.GONE
            listContainer059.visibility = View.VISIBLE

            val inflater059 = LayoutInflater.from(requireContext())
            for (item059 in items059) {
                val rowBinding059 = ItemStokTerendahRow059Binding.inflate(inflater059, listContainer059, false)
                rowBinding059.tvNamaStokTerendah059.text = item059.nama059
                rowBinding059.tvJumlahStokTerendah059.text = getString(R.string.tersisa_format, item059.jumlah059)

                rowBinding059.root.setOnClickListener {
                    val intent059 = Intent(requireContext(), ItemFormActivity059::class.java).apply {
                        putExtra(ItemFormActivity059.EXTRA_ITEM_ID_059, item059.id059)
                    }
                    startActivity(intent059)
                }

                listContainer059.addView(rowBinding059.root)
            }
        }
    }

    private fun renderKategoriSummary059(summaryList059: List<KategoriSummary059>) {
        val listContainer059 = binding059.layoutKategoriSummaryList059
        listContainer059.removeAllViews()

        if (summaryList059.isEmpty()) {
            binding059.tvEmptyKategoriSummary059.visibility = View.VISIBLE
            listContainer059.visibility = View.GONE
        } else {
            binding059.tvEmptyKategoriSummary059.visibility = View.GONE
            listContainer059.visibility = View.VISIBLE

            val inflater059 = LayoutInflater.from(requireContext())
            for (summary059 in summaryList059) {
                val rowBinding059 = ItemKategoriSummaryRow059Binding.inflate(inflater059, listContainer059, false)
                rowBinding059.tvNamaKategoriSummary059.text = summary059.kategori059
                rowBinding059.tvJumlahBarangKategori059.text = getString(R.string.jumlah_barang_kategori_format, summary059.count059)

                // Tap kategori membuka tab Barang dengan filter kategori tersebut aktif
                rowBinding059.root.setOnClickListener {
                    itemViewModel059.setCategoryFilter059(summary059.kategori059)
                    (activity as? MainActivity059)?.navigateToTab059(R.id.nav_barang_059)
                }

                listContainer059.addView(rowBinding059.root)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding059 = null
    }
}
