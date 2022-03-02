package com.pieaksoft.event.consumer.android.enums

enum class Timezone(val value: String) {
    UTC("US_CENTRAL"),
    LOCAL("LOCAL");

    companion object {
        val DEFAULT = LOCAL
        fun findByValue(timezone: String) =
            values().find { it.value == timezone } ?: DEFAULT
    }
}