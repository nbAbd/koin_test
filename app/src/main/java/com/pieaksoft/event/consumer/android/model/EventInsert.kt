package com.pieaksoft.event.consumer.android.model

enum class EventInsertType(val type: String) {
    Off("DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY"),
    Sleep("DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH"),
    Driving("DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING"),
    On("DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING"),
}