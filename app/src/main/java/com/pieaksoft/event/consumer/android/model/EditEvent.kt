package com.pieaksoft.event.consumer.android.model

sealed class EditEvent {
    class Header(
        var titles: List<String> = listOf(
            "Type",
            "Time",
            "Duration",
            "Tr",
            "Sh",
            "Edit"
        )
    ) : EditEvent()

    class Content(var event: Event) : EditEvent()
}
