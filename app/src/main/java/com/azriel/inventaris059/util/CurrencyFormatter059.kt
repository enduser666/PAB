package com.azriel.inventaris059.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter059 {

    private val locale059 = Locale("id", "ID")

    fun format059(amount: Double): String {
        val nf = NumberFormat.getInstance(locale059)
        nf.maximumFractionDigits = 0
        return "Rp ${nf.format(amount)}"
    }
}
