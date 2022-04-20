package com.pieaksoft.event.consumer.android.utils

import java.util.concurrent.TimeUnit
import kotlin.math.abs

fun hmsTimeFormatter(millis: Long, withSeconds: Boolean = false): String {
    val hours = abs(TimeUnit.MILLISECONDS.toHours(millis))
    val min = abs(
        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(millis)
        )
    )
    val sec = abs(
        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(millis)
        )
    )
    var format = "%02d:%02d"
    if (millis < 0) format = "-$format"
    return if (withSeconds) {
        format += ":%02d"
        String.format(format, hours, min, sec)
    } else
        String.format(format, hours, min)
}


fun hmsTimeFormatter2(millis: Long, withSeconds: Boolean = false): String {
    val hours = abs(TimeUnit.MILLISECONDS.toHours(millis))
    val min = abs(
        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(millis)
        )
    )
    val sec = abs(
        TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(millis)
        )
    )
    var format = "%02d h %02d m"
    if (millis < 0) format = "-$format"
    return if (withSeconds) {
        format += ":%02d"
        String.format(format, hours, min, sec)
    } else
        String.format(format, hours, min)
}

fun hmTimeFormatter(millis: Long): String {
    val hours = abs(TimeUnit.MILLISECONDS.toHours(millis))
    val min = abs(
        TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(
            TimeUnit.MILLISECONDS.toHours(millis)
        )
    )
    return String.format(hours, min)
}

private fun String.Companion.format(vararg args: Long): String {
    val hours = args[0]
    val min = args[1]
    return if (hours == 0L) {
        String.format("%02dm", min)
    } else {
        String.format("%dh %02dm", hours, min)
    }
}