package com.pieaksoft.event.consumer.android.utils

import com.pieaksoft.event.consumer.android.model.Event

object Storage {
    var eventList: List<Event> = emptyList()
    var eventListGroupByDate: Map<String, List<Event>> = mapOf()


}