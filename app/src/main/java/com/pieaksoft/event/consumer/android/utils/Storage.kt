package com.pieaksoft.event.consumer.android.utils

import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.model.event.Event

object Storage {
    var eventList: List<Event> = emptyList()
    var eventListGroupByDate: Map<String, List<Event>> = mapOf()
    var eventListMock: MutableList<String> = mutableListOf()
    var isNetworkEnable: Boolean = true
}

val List<Event>.lastItemEventCode: EventCode
    get() {
        if (isEmpty()) return EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY
        return EventCode.findByCode(lastItem.eventCode!!)
    }

val List<Event>.lastItem: Event
    get() = takeWhile { it.eventCode != null }.last()