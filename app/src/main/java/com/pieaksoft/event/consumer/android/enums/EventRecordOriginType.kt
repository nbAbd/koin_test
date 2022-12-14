package com.pieaksoft.event.consumer.android.enums

enum class EventRecordOriginType(val type: String) {
    AUTOMATICALLY_RECORDED_BY_ELD("AUTOMATICALLY_RECORDED_BY_ELD"),
    EDITED_OR_ENTERED_BY_THE_DRIVER("EDITED_OR_ENTERED_BY_THE_DRIVER"),
    EDIT_REQUESTED_BY_AN_AUTHENTICATED_USER_OTHER_THAN_THE_DRIVER("EDIT_REQUESTED_BY_AN_AUTHENTICATED_USER_OTHER_THAN_THE_DRIVER"),
    ASSUMED_FROM_UNIDENTIFIED_DRIVER_PROFILE("ASSUMED_FROM_UNIDENTIFIED_DRIVER_PROFILE"),
    NONE("NONE");
}