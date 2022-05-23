package com.pieaksoft.event.consumer.android.utils

import android.annotation.SuppressLint
import com.pieaksoft.event.consumer.android.enums.Timezone
import com.pieaksoft.event.consumer.android.events.EventViewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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

/**
 * Method calculates number of days between two dates
 *
 * @param [start]  Start date
 * @param [zoneId] ZoneId used to determine user's timezone
 * @param [end] End date
 *
 * @return [Int] - number of days between dates
 */
fun daysBetweenDates(
    start: String,
    end: String? = null,
    zoneId: ZoneId
): Int {
    val formatter =
        DateTimeFormatter.ofPattern("${EventViewModel.DATE_FORMAT_yyyy_MM_dd} ${EventViewModel.TIME_FORMAT_HH_mm}")
    val startDate = LocalDateTime.parse(start, formatter)

    // If end date is null, we assume that we should get current date
    val endDate = if (end == null) {
        LocalDateTime.now(zoneId)
    } else {
        LocalDateTime.parse(end, formatter)
    }
    return ChronoUnit.DAYS.between(startDate, endDate).toInt()
}

fun Date.formatToServerDateDefaults2(): String {
    val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.US)
    return sdf.format(this)
}