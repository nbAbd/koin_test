package com.pieaksoft.event.consumer.android.utils.graph


import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.model.event.Event
import kotlin.math.abs

class EventDataSet(entries: MutableList<Entry>?) : LineDataSet(entries, "") {
    companion object {
        operator fun invoke(events: MutableList<Event>): EventDataSet {
            val converter = Converter(events = events)
            val entries = converter.convert()
            return EventDataSet(entries)
        }
    }

    internal class Converter(events: MutableList<Event>) {
        // Sort events by time to draw in chart
        private val sortedEvents = events.sortedBy { it.time }

        // Convert events to chart entries
        fun convert(): MutableList<Entry> {
            val convertedEntries = mutableListOf<Entry>()
            sortedEvents.forEach {
                convertedEntries.add(Entry(it.startGraphTime(), it.yAxis()))
                convertedEntries.add(Entry(it.endGraphTime(), it.yAxis()))
            }
            return convertedEntries
        }


        private fun Event.startGraphTime(): Float {
            val hm = time!!.split(":")
            val hour = hm.first().toInt()
            val min = hm.last().toFloat()

            val graphMinute = (min * 100) / 60

            var formattedGraphMinutes = if (graphMinute < 10) "0$graphMinute" else graphMinute

            formattedGraphMinutes = formattedGraphMinutes.toString().replace(".", "")

            return "$hour.$formattedGraphMinutes".toFloat()
        }

        private fun Event.endGraphTime(): Float {
            val startHourMinute = time!!.split(":")
            val startHour = startHourMinute.first().toInt()

            val endHourMinute = endTime!!.split(":")
            val endHour = endHourMinute.first().toInt()
            val calculatedHour = when {
                startHour > endHour -> startHour + abs(startHour - endHour)
                else -> endHour
            }
            val min = endHourMinute.last().toFloat()

            val graphMinute = (min * 100) / 60

            var formattedGraphMinutes = if (graphMinute < 10) "0$graphMinute" else graphMinute

            formattedGraphMinutes = formattedGraphMinutes.toString().replace(".", "")

            return "$calculatedHour.$formattedGraphMinutes".toFloat()
        }
    }
}

fun Event.yAxis(): Float {
    if (eventCode == null) return 0F
    return when (EventCode.findByCode(code = eventCode!!)) {
        EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING,
        EventCode.DRIVER_INDICATES_YARD_MOVES -> 1F

        EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING -> 2F

        EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH -> 3F

        EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY,
        EventCode.DRIVER_INDICATES_AUTHORIZED_PERSONAL_USE_OF_CMV -> 4F

        else -> 0F
    }
}

