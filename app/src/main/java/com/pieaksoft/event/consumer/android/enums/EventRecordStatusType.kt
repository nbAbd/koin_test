package com.pieaksoft.event.consumer.android.enums

/**
 * An attribute for the event record indicating whether an event is active or inactive and further,
 * if inactive, whether it is due to a change or lack of confirmation by the driver or due to a driver's rejection of change request.
 */
enum class EventRecordStatusType(val type: String) {
    ACTIVE("ACTIVE"),
    INACTIVE_CHANGED("INACTIVE_CHANGED"),
    INACTIVE_CHANGE_REQUESTED("INACTIVE_CHANGE_REQUESTED"),
    INACTIVE_CHANGE_REJECTED("INACTIVE_CHANGE_REJECTED"),
    NONE("NONE");
}