package com.pieaksoft.event.consumer.android.utils

import java.text.SimpleDateFormat
import java.util.*


fun Date.formatToServerDateDefaults(): String{
    val sdf= SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(this)
}

fun Date.formatToServerTimeDefaults(): String{
    val sdf= SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

fun Date.add(field: Int, amount: Int): Date {
    Calendar.getInstance().apply {
        time = this@add
        add(field, amount)
        return time
    }
}

fun Date.addYears(years: Int): Date{
    return add(Calendar.YEAR, years)
}
fun Date.addMonths(months: Int): Date {
    return add(Calendar.MONTH, months)
}
fun Date.addDays(days: Int): Date{
    return add(Calendar.DAY_OF_MONTH, days)
}
fun Date.addHours(hours: Int): Date{
    return add(Calendar.HOUR_OF_DAY, hours)
}
fun Date.addMinutes(minutes: Int): Date{
    return add(Calendar.MINUTE, minutes)
}
fun Date.addSeconds(seconds: Int): Date{
    return add(Calendar.SECOND, seconds)
}

fun String.getDateFromString(): Date{
    val dateFormat =  SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return dateFormat.parse(this)
}

fun Date.formatToServerDateDefaults2(): String{
    val sdf= SimpleDateFormat("dd-MMM-yyyy", Locale.US)
    return sdf.format(this)
}

fun getCurrentTime() = Calendar.getInstance().time.time / 1000