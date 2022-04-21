package com.pieaksoft.event.consumer.android.enums

enum class RecordOriginType(val type: String) {
    AUTOMATIC("AUTOMATIC"),
    SELF_MANUAL("SELF_MANUAL"),
    EXTERNAL_MANUAL("EXTERNAL_MANUAL"),
    UNIDENTIFIED_DRIVER("UNIDENTIFIED_DRIVER"),
    NONE("NONE");
}