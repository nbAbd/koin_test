package com.pieaksoft.event.consumer.android.model.report

data class Report(
    var reportType: ReportType = ReportType.WEB_SERVICE,
    var comment: String = ""
)

enum class ReportType {
    WEB_SERVICE, FMCSA_EMAIL;

    companion object {
        private val DEFAULT = WEB_SERVICE

        fun forPosition(pos: Int): ReportType {
            return when (pos) {
                0 -> WEB_SERVICE
                1 -> FMCSA_EMAIL
                else -> DEFAULT
            }
        }
    }
}

