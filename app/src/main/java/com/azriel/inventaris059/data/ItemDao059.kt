package com.azriel.inventaris059.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

data class KategoriSummary059(
    val kategori059: String,
    val count059: Int
)

@Dao
interface ItemDao059 {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert059(item059: Item059): Long

    @Update
    suspend fun update059(item059: Item059)

    // Isolasi data: hapus hanya jika id dan userId cocok
    @Query("DELETE FROM item_059 WHERE id059 = :id059 AND userId059 = :userId059")
    suspend fun delete059(id059: Long, userId059: Long)

    // Daftar semua item milik user yang login
    @Query("SELECT * FROM item_059 WHERE userId059 = :userId059 ORDER BY nama059 ASC")
    fun getAllByUser059(userId059: Long): LiveData<List<Item059>>

    @Query("SELECT * FROM item_059 WHERE id059 = :id059 AND userId059 = :userId059 LIMIT 1")
    suspend fun getItemByIdAndUser059(id059: Long, userId059: Long): Item059?

    // Total nilai inventaris: SUM(jumlah059 * harga059)
    @Query("SELECT SUM(jumlah059 * harga059) FROM item_059 WHERE userId059 = :userId059")
    fun getTotalNilai059(userId059: Long): LiveData<Double?>

    // Jenis barang (jumlah item berbeda)
    @Query("SELECT COUNT(*) FROM item_059 WHERE userId059 = :userId059")
    fun getJenisBarang059(userId059: Long): LiveData<Int>

    // Total stok (penjumlahan seluruh unit stok)
    @Query("SELECT SUM(jumlah059) FROM item_059 WHERE userId059 = :userId059")
    fun getTotalStok059(userId059: Long): LiveData<Int?>

    // Jumlah item dengan stok menipis (jumlah > 0 AND jumlah <= threshold)
    @Query("SELECT COUNT(*) FROM item_059 WHERE userId059 = :userId059 AND jumlah059 > 0 AND jumlah059 <= :threshold059")
    fun getStokMenipisCount059(userId059: Long, threshold059: Int): LiveData<Int>

    // Jumlah item dengan stok habis (jumlah = 0)
    @Query("SELECT COUNT(*) FROM item_059 WHERE userId059 = :userId059 AND jumlah059 = 0")
    fun getStokHabisCount059(userId059: Long): LiveData<Int>

    // 3 barang dengan stok terendah
    @Query("SELECT * FROM item_059 WHERE userId059 = :userId059 ORDER BY jumlah059 ASC, nama059 ASC LIMIT 3")
    fun getStokTerendah059(userId059: Long): LiveData<List<Item059>>

    // Daftar kategori unik (DISTINCT) milik user yang login
    @Query("SELECT DISTINCT kategori059 FROM item_059 WHERE userId059 = :userId059 AND kategori059 IS NOT NULL AND TRIM(kategori059) != '' ORDER BY kategori059 ASC")
    fun getDistinctKategori059(userId059: Long): LiveData<List<String>>

    // Jumlah kategori unik
    @Query("SELECT COUNT(DISTINCT kategori059) FROM item_059 WHERE userId059 = :userId059 AND kategori059 IS NOT NULL AND TRIM(kategori059) != ''")
    fun getJumlahKategori059(userId059: Long): LiveData<Int>

    // Ringkasan maksimal 4 kategori teratas dengan jumlah barang terbanyak
    @Query("SELECT kategori059, COUNT(*) as count059 FROM item_059 WHERE userId059 = :userId059 AND kategori059 IS NOT NULL AND TRIM(kategori059) != '' GROUP BY kategori059 ORDER BY count059 DESC, kategori059 ASC LIMIT 4")
    fun getTopKategoriSummary059(userId059: Long): LiveData<List<KategoriSummary059>>
}
