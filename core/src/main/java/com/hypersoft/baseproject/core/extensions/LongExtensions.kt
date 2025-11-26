package com.hypersoft.baseproject.core.extensions

import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10

// ===================== Long Extensions =====================

/**
 * Converts Long to human-readable file size
 * Example: 1024L -> "1 KB"
 */
fun Long.toFileSize(): String {
    if (this <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(this.toDouble()) / log10(1024.0)).toInt()
    return String.format(Locale.getDefault(), "%.1f %s", this / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

/**
 * Converts Long (milliseconds) to formatted time string
 * Example: 3600000L -> "01:00:00"
 */
fun Long.toTimeFormat(): String {
    val hours = this / 3600000
    val minutes = (this % 3600000) / 60000
    val seconds = (this % 60000) / 1000
    return when (hours > 0L) {
        true -> String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        false -> String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}

/**
 * Converts timestamp (ms) to date string with given pattern
 */
fun Long.toDate(pattern: String = "dd/MM/yyyy HH:mm:ss", locale: Locale = Locale.getDefault()): String {
    val sdf = SimpleDateFormat(pattern, locale)
    return sdf.format(Date(this))
}

/**
 * Checks if a Long value is within a given range (inclusive)
 */
fun Long.isInRange(min: Long, max: Long): Boolean = this in min..max

/**
 * Converts Long to Int safely (with rounding if needed)
 */
fun Long.toIntSafe(): Int = this.coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()

/**
 * Converts Long (ms) to seconds as Float
 */
fun Long.toSeconds(): Float = this / 1000f

/**
 * Converts Long (ms) to minutes as Float
 */
fun Long.toMinutes(): Float = this / 60000f

/**
 * Converts Long (ms) to hours as Float
 */
fun Long.toHours(): Float = this / 3600000f

/**
 * Adds commas for large numbers
 * Example: 1000000L -> "1,000,000"
 */
fun Long.formatWithComma(): String {
    val formatter = DecimalFormat("#,###")
    return formatter.format(this)
}

/**
 * Converts Long to ordinal string
 * Example: 1L -> "1st", 2L -> "2nd", 3L -> "3rd", 4L -> "4th"
 */
fun Long.toOrdinal(): String {
    val suffix = when {
        this % 100 in 11..13 -> "th"
        this % 10 == 1L -> "st"
        this % 10 == 2L -> "nd"
        this % 10 == 3L -> "rd"
        else -> "th"
    }
    return "$this$suffix"
}

// ===================== Int Extensions =====================

fun Int.toLongSafe(): Long = this.toLong()

fun Int.formatWithComma(): String = this.toLong().formatWithComma()

// ===================== Float / Double Extensions =====================

fun Float.formatDecimal(digits: Int = 2): String = "%.${digits}f".format(this)
fun Double.formatDecimal(digits: Int = 2): String = "%.${digits}f".format(this)