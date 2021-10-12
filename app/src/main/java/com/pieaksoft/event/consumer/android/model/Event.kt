package com.pieaksoft.event.consumer.android.model

import java.io.Serializable

data class Event(
    val id: String,
    val eventType: String?,
    val eventCode: String?,
    val date: String?,
    val coordinates: Location?,
    val shippingDocumentNumber: String?,
    val totalEngineHours: Int?,
    val totalEngineMiles: Int?,
    val eventRecordOrigin: String?,
    val eventRecordStatus: String?,
    val malfunctionIndicatorStatus: String?,
    val dataDiagnosticEventIndicatorStatus: String?,
    val driverLocationDescription: String?,
    val dutyStatus: String?,
    val certification: Certification?,
    val recordOrigin: String?,
    val createdAt: String?,
    val distanceSinceLastValidCoordinates: String?,
    val eventSequenceId: String?,
): Serializable {
}