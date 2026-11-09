package com.azriel.inventaris059.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao059 {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert059(user: User059): Long

    @Query("SELECT * FROM user_059 WHERE username059 = :username059 LIMIT 1")
    suspend fun findByUsername059(username059: String): User059?

    @Query("SELECT * FROM user_059 WHERE id059 = :id059 LIMIT 1")
    suspend fun findById059(id059: Long): User059?

    @Query("SELECT * FROM user_059 WHERE id059 = :id059 LIMIT 1")
    fun observeById059(id059: Long): LiveData<User059?>

    @Query("SELECT COUNT(*) FROM user_059 WHERE username059 = :username059")
    suspend fun countByUsername059(username059: String): Int

    @Query("SELECT COUNT(*) FROM user_059 WHERE username059 = :username059 AND id059 != :currentUserId059")
    suspend fun countByUsernameForOtherUser059(username059: String, currentUserId059: Long): Int

    @Update
    suspend fun update059(user: User059)
}
