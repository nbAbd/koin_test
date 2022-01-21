package com.pieaksoft.event.consumer.android.model

data class EventCalculationModel(
    var totalLimit: Long = 0,
    var remainMillis: Long = 0,
    var startTime: Long = 0
)