package com.pieaksoft.event.consumer.android.model.md

import com.pieaksoft.event.consumer.android.model.md.MD.DiagnosticsCategory
import com.pieaksoft.event.consumer.android.model.md.MD.DiagnosticsContent

sealed class MD {
    class DiagnosticsCategory(var categoryName: String = "") : MD()
    class DiagnosticsContent(var content: String = "", var isFailed: Boolean = false) : MD()
}

object DataGenerator {
    fun createMD(): List<MD> {
        return listOf(
            DiagnosticsCategory(categoryName = "Data Diagnostics:"),
            DiagnosticsContent(content = "Power data diagnostic", isFailed = false),
            DiagnosticsContent(
                content = "Engine synchronization data diagnostic",
                isFailed = false
            ),
            DiagnosticsContent(content = "Missing required data elements", isFailed = false),
            DiagnosticsContent(content = "Data transfer data diagnostic", isFailed = false),
            DiagnosticsContent(content = "Unidentified driving records", isFailed = true),
            DiagnosticsContent(content = "Positioning data diagnostic", isFailed = false),


            // second category
            DiagnosticsCategory(categoryName = "Malfunctions:"),
            DiagnosticsContent(content = "Power compliance", isFailed = false),
            DiagnosticsContent(
                content = "Engine synchronization data diagnostic",
                isFailed = false
            ),
            DiagnosticsContent(content = "Missing required data elements", isFailed = false)
        )
    }
}

