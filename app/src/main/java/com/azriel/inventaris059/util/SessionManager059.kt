package com.azriel.inventaris059.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager059(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME_059, Context.MODE_PRIVATE)

    fun saveUserId059(userId059: Long) {
        prefs.edit().putLong(KEY_USER_ID_059, userId059).apply()
    }

    fun getUserId059(): Long = prefs.getLong(KEY_USER_ID_059, NO_USER_059)

    fun isLoggedIn059(): Boolean = getUserId059() != NO_USER_059

    fun clearSession059() {
        prefs.edit().remove(KEY_USER_ID_059).apply()
    }

    companion object {
        private const val PREF_NAME_059 = "session_prefs_059"
        private const val KEY_USER_ID_059 = "user_id_059"
        const val NO_USER_059 = -1L
    }
}
