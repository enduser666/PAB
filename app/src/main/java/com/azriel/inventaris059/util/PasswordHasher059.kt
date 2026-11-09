package com.azriel.inventaris059.util

import java.security.MessageDigest

object PasswordHasher059 {

    fun hash059(plain: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(plain.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
