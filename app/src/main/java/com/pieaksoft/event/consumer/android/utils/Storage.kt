package com.pieaksoft.event.consumer.android.utils

import com.pieaksoft.event.consumer.android.model.event.Event

object Storage {
    var eventList: List<Event> = emptyList()
    var eventListGroupByDate: Map<String, List<Event>> = mapOf()
    var eventListMock: MutableList<String> = mutableListOf()
    var isNetworkEnable: Boolean = true
}