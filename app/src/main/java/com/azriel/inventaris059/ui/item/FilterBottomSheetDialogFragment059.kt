package com.azriel.inventaris059.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.azriel.inventaris059.R
import com.azriel.inventaris059.databinding.BottomSheetFilter059Binding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class FilterBottomSheetDialogFragment059(
    private val currentStockOption059: StockFilterOption059,
    private val currentSortOption059: SortOption059,
    private val currentCategory059: String?,
    private val userCategories059: List<String>,
    private val onApply059: (StockFilterOption059, SortOption059, String?) -> Unit,
    private val onReset059: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding059: BottomSheetFilter059Binding? = null
    private val binding059 get() = _binding059!!

    private var selectedCategory059: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding059 = BottomSheetFilter059Binding.inflate(inflater, container, false)
        return binding059.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedCategory059 = currentCategory059

        // 1. Radio status stok
        when (currentStockOption059) {
            StockFilterOption059.SEMUA -> binding059.rbStatusSemua059.isChecked = true
            StockFilterOption059.TERSEDIA -> binding059.rbStatusTersedia059.isChecked = true
            StockFilterOption059.MENIPIS -> binding059.rbStatusMenipis059.isChecked = true
            StockFilterOption059.HABIS -> binding059.rbStatusHabis059.isChecked = true
        }

        // 2. Radio sort
        when (currentSortOption059) {
            SortOption059.NAMA_ASC -> binding059.rbSortNama059.isChecked = true
            SortOption059.STOK_ASC -> binding059.rbSortStok059.isChecked = true
            SortOption059.HARGA_DESC -> binding059.rbSortHarga059.isChecked = true
        }

        // 3. Dynamic Category Chips
        setupCategoryChips059()

        // 4. Button Terapkan
        binding059.btnTerapkanFilter059.setOnClickListener {
            val selectedStock059 = when (binding059.rgStatusStok059.checkedRadioButtonId) {
                binding059.rbStatusTersedia059.id -> StockFilterOption059.TERSEDIA
                binding059.rbStatusMenipis059.id -> StockFilterOption059.MENIPIS
                binding059.rbStatusHabis059.id -> StockFilterOption059.HABIS
                else -> StockFilterOption059.SEMUA
            }

            val selectedSort059 = when (binding059.rgUrutkan059.checkedRadioButtonId) {
                binding059.rbSortStok059.id -> SortOption059.STOK_ASC
                binding059.rbSortHarga059.id -> SortOption059.HARGA_DESC
                else -> SortOption059.NAMA_ASC
            }

            onApply059(selectedStock059, selectedSort059, selectedCategory059)
            dismiss()
        }

        // 5. Button Reset
        binding059.btnResetFilter059.setOnClickListener {
            onReset059()
            dismiss()
        }
    }

    private fun setupCategoryChips059() {
        val chipGroup059 = binding059.chipGroupKategori059
        val chipSemua059 = binding059.chipKategoriSemua059

        chipSemua059.isChecked = currentCategory059.isNullOrBlank()
        chipSemua059.setOnClickListener {
            selectedCategory059 = null
        }

        for (kategori059 in userCategories059) {
            val chip059 = Chip(requireContext()).apply {
                text = kategori059
                isCheckable = true
                isChecked = kategori059.equals(currentCategory059, ignoreCase = true)
                setOnClickListener {
                    selectedCategory059 = kategori059
                }
            }
            chipGroup059.addView(chip059)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding059 = null
    }

    companion object {
        const val TAG059 = "FilterBottomSheet059"
    }
}
