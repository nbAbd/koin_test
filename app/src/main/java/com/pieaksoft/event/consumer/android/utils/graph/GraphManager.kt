package com.pieaksoft.event.consumer.android.utils.graph

object GraphManager {
    val yAxisLabels = arrayOf("", "On", "D", "SB", "Off", "")

    val xAxisLabels = arrayListOf<String>().apply {
        add("M")
        repeat(24) { add(it.inc().toString()) }
    }
}