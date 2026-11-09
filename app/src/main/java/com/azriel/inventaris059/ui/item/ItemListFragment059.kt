package com.azriel.inventaris059.ui.item

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.azriel.inventaris059.R
import com.azriel.inventaris059.adapter.ItemAdapter059
import com.azriel.inventaris059.data.Item059
import com.azriel.inventaris059.databinding.FragmentItemList059Binding
import com.azriel.inventaris059.ui.MainActivity059
import com.azriel.inventaris059.util.SessionManager059
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class ItemListFragment059 : Fragment() {

    private var _binding059: FragmentItemList059Binding? = null
    private val binding059 get() = _binding059!!

    private val viewModel059: ItemViewModel059 by activityViewModels()
    private lateinit var sessionManager059: SessionManager059
    private lateinit var adapter059: ItemAdapter059

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding059 = FragmentItemList059Binding.inflate(inflater, container, false)
        return binding059.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager059 = SessionManager059(requireContext())
        val userId059 = sessionManager059.getUserId059()
        viewModel059.init059(userId059)

        setupRecyclerView059()
        setupTopBarControls059()
        observeData059()
    }

    private fun setupRecyclerView059() {
        adapter059 = ItemAdapter059(
            onItemClick059 = { item059 ->
                val intent059 = Intent(requireContext(), ItemFormActivity059::class.java).apply {
                    putExtra(ItemFormActivity059.EXTRA_ITEM_ID_059, item059.id059)
                }
                startActivity(intent059)
            },
            onItemLongClick059 = { item059 ->
                showDeleteConfirmDialog059(item059)
            }
        )
        binding059.recyclerView059.adapter = adapter059
    }

    private fun setupTopBarControls059() {
        // Toggle Search Field
        binding059.btnToggleSearch059.setOnClickListener {
            val isVisible059 = binding059.layoutSearchPill059.visibility == View.VISIBLE
            binding059.layoutSearchPill059.visibility = if (isVisible059) View.GONE else View.VISIBLE
            if (isVisible059) {
                binding059.etSearch059.setText("")
                viewModel059.searchQuery059.value = ""
            } else {
                binding059.etSearch059.requestFocus()
            }
        }

        // Live text watcher untuk search filter (mencari nama DAN kategori)
        binding059.etSearch059.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel059.searchQuery059.value = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Tombol Pill "+ Tambah Barang" pindah ke tab Tambah
        binding059.btnPillTambah059.setOnClickListener {
            (activity as? MainActivity059)?.navigateToTab059(R.id.nav_tambah_059)
        }

        // Tombol Empty State Tambah
        binding059.btnEmptyKeTambah059.setOnClickListener {
            (activity as? MainActivity059)?.navigateToTab059(R.id.nav_tambah_059)
        }

        // Tombol Filter membuka Bottom Sheet
        binding059.btnFilter059.setOnClickListener {
            val userCats059 = viewModel059.userCategories059.value ?: emptyList()
            val bottomSheet059 = FilterBottomSheetDialogFragment059(
                currentStockOption059 = viewModel059.filterStock059.value ?: StockFilterOption059.SEMUA,
                currentSortOption059 = viewModel059.sortOption059.value ?: SortOption059.NAMA_ASC,
                currentCategory059 = viewModel059.filterCategory059.value,
                userCategories059 = userCats059,
                onApply059 = { stock059, sort059, cat059 ->
                    viewModel059.filterStock059.value = stock059
                    viewModel059.sortOption059.value = sort059
                    viewModel059.filterCategory059.value = cat059
                },
                onReset059 = {
                    viewModel059.resetFilter059()
                }
            )
            bottomSheet059.show(childFragmentManager, FilterBottomSheetDialogFragment059.TAG059)
        }

        // Reset filter pada empty search
        binding059.btnResetFilterEmpty059.setOnClickListener {
            binding059.etSearch059.setText("")
            viewModel059.searchQuery059.value = ""
            viewModel059.resetFilter059()
        }
    }

    private fun observeData059() {
        viewModel059.filteredItems059.observe(viewLifecycleOwner) { items059 ->
            adapter059.submitList(items059)

            val rawList059 = viewModel059.rawItems059.value ?: emptyList()
            updateEmptyState059(rawList059.isEmpty(), items059.isEmpty())
        }

        viewModel059.rawItems059.observe(viewLifecycleOwner) { rawList059 ->
            val filteredList059 = viewModel059.filteredItems059.value ?: emptyList()
            updateEmptyState059(rawList059.isEmpty(), filteredList059.isEmpty())
        }

        // Indikator aktif pada tombol filter
        viewModel059.filterStock059.observe(viewLifecycleOwner) { updateFilterDot059() }
        viewModel059.sortOption059.observe(viewLifecycleOwner) { updateFilterDot059() }
        viewModel059.filterCategory059.observe(viewLifecycleOwner) { updateFilterDot059() }
    }

    private fun updateFilterDot059() {
        val isActive059 = viewModel059.isFilterActive059()
        binding059.dotFilterActive059.visibility = if (isActive059) View.VISIBLE else View.GONE
    }

    private fun updateEmptyState059(isRawEmpty059: Boolean, isFilteredEmpty059: Boolean) {
        if (isRawEmpty059) {
            binding059.recyclerView059.visibility = View.GONE
            binding059.layoutEmptyList059.visibility = View.VISIBLE
            binding059.layoutEmptySearch059.visibility = View.GONE
        } else if (isFilteredEmpty059) {
            binding059.recyclerView059.visibility = View.GONE
            binding059.layoutEmptyList059.visibility = View.GONE
            binding059.layoutEmptySearch059.visibility = View.VISIBLE
        } else {
            binding059.recyclerView059.visibility = View.VISIBLE
            binding059.layoutEmptyList059.visibility = View.GONE
            binding059.layoutEmptySearch059.visibility = View.GONE
        }
    }

    private fun showDeleteConfirmDialog059(item059: Item059) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.hapus_konfirmasi_judul))
            .setMessage(getString(R.string.hapus_konfirmasi_pesan))
            .setPositiveButton(getString(R.string.hapus)) { _, _ ->
                val userId059 = sessionManager059.getUserId059()
                viewModel059.deleteItem059(item059.id059, userId059) {
                    Snackbar.make(binding059.root, getString(R.string.msg_barang_dihapus), Snackbar.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(getString(R.string.batal), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding059 = null
    }
}
