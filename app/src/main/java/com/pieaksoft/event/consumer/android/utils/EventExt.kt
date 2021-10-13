package com.pieaksoft.event.consumer.android.utils

import com.pieaksoft.event.consumer.android.model.Event

fun Event.getCode(): String{
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