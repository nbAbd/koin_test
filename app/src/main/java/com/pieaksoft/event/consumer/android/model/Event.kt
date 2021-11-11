package com.pieaksoft.event.consumer.android.model

import java.io.Serializable

data class Event(
    val id: String? = "",
    var eventType: String?= "",
    var eventCode: String?= "",
    var date: String?= "",
    val time: String?= "",
    val coordinates: Location? = Location(0f,0f),
    val shippingDocumentNumber: String?= "",
    val totalEngineHours: Int?=0,
    val totalEngineMiles: Int?=0,
    val eventRecordOrigin: String?= "",
    val eventRecordStatus: String?= "",
    val malfunctionIndicatorStatus: String?= "",
    val dataDiagnosticEventIndicatorStatus: String?= "",
    val driverLocationDescription: String?= "",
    val dutyStatus: String?= "",
    var certification: Certification? = null,
    var certifyDate: List<Certification>? = null,
    val recordOrigin: String?= "",
    val createdAt: String?= "",
    val distanceSinceLastValidCoordinates: String?= "",
    val eventSequenceId: String?= "",
): Serializable {
    var endDate: String?= ""
    var endTime: String?= "24:00"
}