package com.azriel.inventaris059.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_059",
    indices = [Index(value = ["username059"], unique = true)]
)
data class User059(
    @PrimaryKey(autoGenerate = true)
    val id059: Long = 0,
    val username059: String,
    val password059: String
)
