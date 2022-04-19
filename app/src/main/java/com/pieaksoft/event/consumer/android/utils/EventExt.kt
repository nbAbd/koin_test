package com.pieaksoft.event.consumer.android.utils

import com.pieaksoft.event.consumer.android.model.event.Event
import java.util.concurrent.TimeUnit
import kotlin.math.abs

fun Event.getCode(): String {
    return when {
        eventCode?.equals("DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY") == true -> {
            "Off"
        }
        eventCode?.equals("DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH") == true -> {
            "SB"
        }
        eventCode?.equals("DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING") == true -> {
            "D"
        }
        eventCode?.equals("DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING") == true -> {
            "On"
        }
        else -> {
            ""
        }
    }
}

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