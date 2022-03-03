package com.pieaksoft.event.consumer.android.enums

enum class Timezone {
    US_CENTRAL {
        override val value: String = "America/Chicago"
    },
    US_EASTERN {
        override val value: String = "America/Bogota"
    },
    US_MOUNTAIN {
        override val value: String = "America/Boise"
    },
    US_PACIFIC {
        override val value: String = "America/Los_Angeles"
    };

    abstract val value: String

    companion object {
        private val DEFAULT = US_CENTRAL
        fun findByName(timezone: String) =
            values().find { it.name == timezone } ?: DEFAULT
    }
}