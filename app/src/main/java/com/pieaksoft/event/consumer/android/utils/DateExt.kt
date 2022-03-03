package com.pieaksoft.event.consumer.android.utils

import android.annotation.SuppressLint
import com.pieaksoft.event.consumer.android.enums.Timezone
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
fun Date.formatToServerDateDefaults(timezone: Timezone? = null): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    timezone?.let { sdf.timeZone = TimeZone.getTimeZone(it.value) }
    return sdf.format(this)
}

@SuppressLint("SimpleDateFormat")
fun Date.formatToServerTimeDefaults(timezone: Timezone? = null): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    timezone?.let { sdf.timeZone = TimeZone.getTimeZone(it.value) }
    return sdf.format(this)
}

fun Date.add(field: Int, amount: Int): Date {
    Calendar.getInstance().apply {
        time = this@add
        add(field, amount)
        return time
    }
}

fun Date.addYears(years: Int): Date {
    return add(Calendar.YEAR, years)
}

fun Date.addMonths(months: Int): Date {
    return add(Calendar.MONTH, months)
}

fun Date.addDays(days: Int): Date {
    return add(Calendar.DAY_OF_MONTH, days)
}

fun Date.addHours(hours: Int): Date {
    return add(Calendar.HOUR_OF_DAY, hours)
}

fun Date.addMinutes(minutes: Int): Date {
    return add(Calendar.MINUTE, minutes)
}

fun Date.addSeconds(seconds: Int): Date {
    return add(Calendar.SECOND, seconds)
}

fun String.getDateFromString(): Date {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return dateFormat.parse(this)!!
}

fun Date.formatToServerDateDefaults2(): String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
    return sdf.format(this)
}