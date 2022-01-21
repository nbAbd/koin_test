package com.pieaksoft.event.consumer.android.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pieaksoft.event.consumer.android.db.converters.CertificationListConverter
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Entity
data class Event(
    @PrimaryKey
    val id: String = "1",
    var eventType: String? = "",
    var eventCode: String? = "",
    var date: String? = "",
    var time: String? = "",
    @TypeConverters(Location::class)
    val coordinates: Location = Location(0f, 0f),
    val shippingDocumentNumber: String? = "",
    val totalEngineHours: Int? = 0,
    val totalEngineMiles: Int? = 0,
    val eventRecordOrigin: String? = "",
    val eventRecordStatus: String? = "",
    val malfunctionIndicatorStatus: String? = "",
    val dataDiagnosticEventIndicatorStatus: String? = "",
    val driverLocationDescription: String? = "",
    val dutyStatus: String? = "",
    @TypeConverters(Certification::class)
    var certification: Certification?,
    @TypeConverters(CertificationListConverter::class)
    var certifyDate: List<Certification>? = emptyList(),
    val recordOrigin: String? = "",
    val createdAt: String? = "",
    val distanceSinceLastValidCoordinates: String? = "",
    val eventSequenceId: String? = "",
    var endDate: String? = "",
    var endTime: String? = "25:00",
    var durationInMillis: Long = 0L,
    var isSyncWithServer: Boolean? = true
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