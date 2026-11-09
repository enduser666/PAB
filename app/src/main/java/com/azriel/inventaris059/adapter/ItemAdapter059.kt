package com.azriel.inventaris059.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.azriel.inventaris059.R
import com.azriel.inventaris059.data.Item059
import com.azriel.inventaris059.databinding.ItemRow059Binding
import com.azriel.inventaris059.util.Constants059
import com.azriel.inventaris059.util.CurrencyFormatter059
import com.azriel.inventaris059.util.ImageHelper059

class ItemAdapter059(
    private val onItemClick059: (Item059) -> Unit,
    private val onItemLongClick059: (Item059) -> Unit
) : ListAdapter<Item059, ItemAdapter059.ViewHolder059>(DiffCallback059()) {

    inner class ViewHolder059(private val binding059: ItemRow059Binding) :
        RecyclerView.ViewHolder(binding059.root) {

        fun bind059(item059: Item059) {
            val context059 = binding059.root.context

            // Thumbnail Foto / Inisial (Downsampled manual, tanpa beban memori)
            val fotoPath059 = item059.fotoPath059
            if (!fotoPath059.isNullOrBlank()) {
                val bitmap059 = ImageHelper059.loadDownsampledBitmap059(fotoPath059, 144, 144)
                if (bitmap059 != null) {
                    binding059.ivThumbFoto059.setImageBitmap(bitmap059)
                    binding059.ivThumbFoto059.visibility = View.VISIBLE
                    binding059.tvThumbInitial059.visibility = View.GONE
                } else {
                    showInitial059(item059)
                }
            } else {
                showInitial059(item059)
            }

            // Nama barang semi-bold
            binding059.tvNama059.text = item059.nama059

            // Baris Kategori jika terisi
            if (!item059.kategori059.isNullOrBlank()) {
                binding059.tvKategoriBadge059.visibility = View.VISIBLE
                binding059.tvKategoriBadge059.text = context059.getString(R.string.kategori_format, item059.kategori059)
            } else {
                binding059.tvKategoriBadge059.visibility = View.GONE
            }

            // Metrik: Stok, Harga, Subtotal
            val hargaStr059 = CurrencyFormatter059.format059(item059.harga059)
            val subtotal059 = item059.jumlah059.toDouble() * item059.harga059
            val subtotalStr059 = CurrencyFormatter059.format059(subtotal059)

            val stokPart059 = context059.getString(R.string.stok_detail_format, item059.jumlah059)
            val hargaPart059 = context059.getString(R.string.harga_detail_format, hargaStr059)
            val subtotalPart059 = context059.getString(R.string.subtotal_detail_format, subtotalStr059)
            binding059.tvDetailMetrics059.text = "$stokPart059  •  $hargaPart059\n$subtotalPart059"

            // Chip Status Pill
            when {
                item059.jumlah059 == 0 -> {
                    binding059.tvStatusChip059.text = context059.getString(R.string.chip_habis)
                    binding059.tvStatusChip059.setBackgroundResource(R.drawable.bg_panel_gray_059)
                    binding059.tvStatusChip059.setTextColor(ContextCompat.getColor(context059, R.color.color_error_059))
                }
                item059.jumlah059 <= Constants059.LOW_STOCK_THRESHOLD_059 -> {
                    binding059.tvStatusChip059.text = context059.getString(R.string.chip_menipis)
                    binding059.tvStatusChip059.setBackgroundResource(R.drawable.bg_panel_total_059)
                    binding059.tvStatusChip059.setTextColor(ContextCompat.getColor(context059, R.color.color_primary_059))
                }
                else -> {
                    binding059.tvStatusChip059.text = context059.getString(R.string.chip_tersedia)
                    binding059.tvStatusChip059.setBackgroundResource(R.drawable.bg_panel_total_059)
                    binding059.tvStatusChip059.setTextColor(ContextCompat.getColor(context059, R.color.color_primary_dark_059))
                }
            }

            // Click & Long Click listener
            binding059.root.setOnClickListener { onItemClick059(item059) }
            binding059.root.setOnLongClickListener {
                onItemLongClick059(item059)
                true
            }
        }

        private fun showInitial059(item059: Item059) {
            binding059.ivThumbFoto059.setImageDrawable(null)
            binding059.ivThumbFoto059.visibility = View.GONE
            binding059.tvThumbInitial059.visibility = View.VISIBLE
            binding059.tvThumbInitial059.text = item059.nama059.firstOrNull()?.uppercase() ?: "?"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder059 {
        val binding059 = ItemRow059Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder059(binding059)
    }

    override fun onBindViewHolder(holder: ViewHolder059, position: Int) {
        holder.bind059(getItem(position))
    }

    private class DiffCallback059 : DiffUtil.ItemCallback<Item059>() {
        override fun areItemsTheSame(oldItem059: Item059, newItem059: Item059) =
            oldItem059.id059 == newItem059.id059

        override fun areContentsTheSame(oldItem059: Item059, newItem059: Item059) =
            oldItem059 == newItem059
    }
}
