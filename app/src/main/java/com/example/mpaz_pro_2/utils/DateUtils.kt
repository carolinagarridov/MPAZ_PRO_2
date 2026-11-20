package com.example.mpaz_pro_2.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    /** Fecha actual en formato AAAA-MM-DD. */
    fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    /** Fecha y hora actual en formato AAAA-MM-DD HH:mm. */
    fun now(): String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(Date())
}