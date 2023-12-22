package com.bangkit.annaapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateString(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val date: Date = inputFormat.parse(dateString) ?: return ""

    return outputFormat.format(date)
}