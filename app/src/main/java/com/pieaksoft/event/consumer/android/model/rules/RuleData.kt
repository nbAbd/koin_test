package com.pieaksoft.event.consumer.android.model.rules


private val headerTitles = listOf("Clocks", "Times")

sealed class RulesData {
    class Header(var titles: List<String> = headerTitles) : RulesData()
    class Content(var rule: Rule) : RulesData()
}
