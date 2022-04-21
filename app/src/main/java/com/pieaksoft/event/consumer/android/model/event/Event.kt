package com.pieaksoft.event.consumer.android.model.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pieaksoft.event.consumer.android.db.converters.CertificationListConverter
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@Entity
data class Event(
    @PrimaryKey
    val id: String = "1",
    var eventType: String? = "",
    var eventCode: String? = "",
    var date: String? = "",
    var time: String? = "",
    @TypeConverters(Location::class)
    var coordinates: Location = Location(null, null),
    var shippingDocumentNumber: String? = "",
    val totalEngineHours: Int? = 0,
    var totalEngineMiles: Int? = 0,
    var eventRecordOrigin: String? = "",
    var eventRecordStatus: String? = "",
    val malfunctionIndicatorStatus: String? = "",
    val dataDiagnosticEventIndicatorStatus: String? = "",
    var driverLocationDescription: String? = "",
    val dutyStatus: String? = "",
    @TypeConverters(Certification::class)
    var certification: Certification? = null,
    @TypeConverters(CertificationListConverter::class)
    var certifyDate: List<Certification>? = emptyList(),
    val recordOrigin: String? = "",
    val createdAt: String? = "",
    val distanceSinceLastValidCoordinates: String? = "",
    val eventSequenceId: String? = "",
    var endDate: String? = "",
    var endTime: String? = "25:00",
    var durationInMillis: Long = 0L,
    var toAddress: String? = "",
    var fromAddress: String? = "",
    var trailer: String? = "",
    var comment: String? = "",
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

fun Event.formatDuration(): String {
    return String.format(
        "%02dh %02dm %02ds",
        TimeUnit.MILLISECONDS.toHours(durationInMillis),
        TimeUnit.MILLISECONDS.toMinutes(durationInMillis) -
                TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(
                        durationInMillis
                    )
                ),
        TimeUnit.MILLISECONDS.toSeconds(durationInMillis) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(
                durationInMillis
            )
        )
    )
}

fun Event.isLocationSet() = coordinates.latitude != null && coordinates.longitude != null