package com.pieaksoft.event.consumer.android.utils

import android.annotation.SuppressLint
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.model.event.Event
import java.text.SimpleDateFormat
import java.util.*

object EventManager {
    var calculationEvents: List<Event> = emptyList()
    val uiEvents: List<Event>
        get() = calculationEvents.filterNot { it.eventCode == EventCode.CYCLE_RESET.code }

    var eventListGroupByDate: Map<String, List<Event>> = mapOf()
    var eventListMock: MutableList<String> = mutableListOf()
    var isNetworkEnable: Boolean = true
}

val List<Event>.lastItemEventCode: EventCode
    get() {
        if (isEmpty()) return EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY
        return when (val lastEventCode = EventCode.findByCode(lastItem.eventCode!!)) {
            EventCode.DRIVER_INDICATES_YARD_MOVES -> EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING
            EventCode.DRIVER_INDICATES_AUTHORIZED_PERSONAL_USE_OF_CMV -> EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY
            else -> lastEventCode
        }
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

val uiEvents = EventManager.uiEvents