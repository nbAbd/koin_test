package com.pieaksoft.event.consumer.android.utils

import android.annotation.SuppressLint
import android.util.Log
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.enums.Timezone
import com.pieaksoft.event.consumer.android.model.event.Event
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object Storage {
    var eventList: List<Event> = emptyList()
    var eventListGroupByDate: Map<String, List<Event>> = mapOf()
    var eventListMock: MutableList<String> = mutableListOf()
    var isNetworkEnable: Boolean = true
}

fun Map<String, List<Event>>.getStartDateTimeOfGivenEvent(event: Event): Event? {
    if (event.time != "00:00") return event
    keys.reversed().forEach { index ->
        if (index < event.date.toString()) {
            this[index]?.last().also {
                if (it?.time != "00:00") return it
            }
        }
    }
    return null
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
            return formatter.parse(last().date + " " + last().time)
        }
        return null
    }

val eventList = Storage.eventList