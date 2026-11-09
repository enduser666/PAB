package com.azriel.inventaris059.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "item_059",
    foreignKeys = [
        ForeignKey(
            entity = User059::class,
            parentColumns = ["id059"],
            childColumns = ["userId059"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId059"])]
)
data class Item059(
    @PrimaryKey(autoGenerate = true)
    val id059: Long = 0,
    val userId059: Long,
    val nama059: String,
    val jumlah059: Int,
    val harga059: Double,
    val fotoPath059: String? = null,
    val deskripsi059: String? = null,
    val kategori059: String? = null
)
