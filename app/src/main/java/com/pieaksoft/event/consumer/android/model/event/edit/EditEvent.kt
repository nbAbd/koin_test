package com.pieaksoft.event.consumer.android.model.event.edit

import com.pieaksoft.event.consumer.android.model.event.Event

private val headerTitles = listOf("Type", "Time", "Duration", "Tr", "Sh", "Edit")

sealed class EditEvent {
    class Header(var titles: List<String> = headerTitles) : EditEvent()
    class Content(var event: Event) : EditEvent()
}
