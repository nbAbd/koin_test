package com.pieaksoft.event.consumer.android.utils

import android.annotation.SuppressLint
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.model.event.Event
import java.text.SimpleDateFormat
import java.util.*

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

val List<Event>.lastItemStartDate: Date?
    @SuppressLint("SimpleDateFormat")
    get() {
        if (isNotEmpty()) {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
            return formatter.parse(lastItem.date + " " + lastItem.time)
        }
        return null
    }

val eventList = Storage.eventList