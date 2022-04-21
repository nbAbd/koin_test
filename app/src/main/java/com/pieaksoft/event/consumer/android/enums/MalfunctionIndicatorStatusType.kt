package com.pieaksoft.event.consumer.android.enums

enum class MalfunctionIndicatorStatusType(val type: String) {
    NO_ACTIVE_MALFUNCTION("NO_ACTIVE_MALFUNCTION"),
    AT_LEAST_ONE_ACTIVE_MALFUNCTION("AT_LEAST_ONE_ACTIVE_MALFUNCTION"),
    NONE("NONE");
}