package com.pieaksoft.event.consumer.android.model

import android.util.Log
import androidx.core.util.rangeTo
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

data class Event(
    val id: String? = "",
    var eventType: String? = "",
    var eventCode: String? = "",
    var date: String? = "",
    var time: String? = "",
    val coordinates: Location? = Location(0f, 0f),
    val shippingDocumentNumber: String? = "",
    val totalEngineHours: Int? = 0,
    val totalEngineMiles: Int? = 0,
    val eventRecordOrigin: String? = "",
    val eventRecordStatus: String? = "",
    val malfunctionIndicatorStatus: String? = "",
    val dataDiagnosticEventIndicatorStatus: String? = "",
    val driverLocationDescription: String? = "",
    val dutyStatus: String? = "",
    var certification: Certification? = null,
    var certifyDate: List<Certification>? = null,
    val recordOrigin: String? = "",
    val createdAt: String? = "",
    val distanceSinceLastValidCoordinates: String? = "",
    val eventSequenceId: String? = "",
    var endDate: String? = "",
    var endTime: String? = "24:00",
    var durationInMillis: Long = 0
) : Serializable {

    fun calculateDuration() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTimeString = "$date $time"
        val startDateTime = LocalDateTime.parse(dateTimeString, formatter)
        val endDateTimeString = "$endDate $endTime"
        val endDateTime = LocalDateTime.parse(endDateTimeString, formatter)
        durationInMillis = startDateTime.until(endDateTime, ChronoUnit.MILLIS)
    }
}