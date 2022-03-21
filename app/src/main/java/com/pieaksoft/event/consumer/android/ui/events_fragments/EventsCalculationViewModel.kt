package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.enums.EventInsertType
import com.pieaksoft.event.consumer.android.enums.Timezone
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.utils.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

const val on_Duty_Cycle_Minutes = 4200
const val on_Duty_Window_Minutes = 840
const val sleeper_Beth_Minutes = 600
const val on_Duty_Break_In_Minutes = 480

class EventsCalculationViewModel(val app: Application, private val eventViewModel: EventViewModel) :
    BaseViewModel(app) {

    private var onDutyCycleMinutes = 4200 // per minute 70 hour
    private var onDutyWindowMinutes = 840 // per minute 14 hour
    private var sleeperBethMinutes = 600 // per minute 10 hour
    private var onDutyBreakInMinutes = 480 // per minute 8 hour
    private var drivingDutyMinutes = 660 // per minute 11 hour
    private var needResetCycleMinutes: Long = 2040 // per minute 34 hour
    private var needThiryMinuteBreak: Boolean = false
    private var needResetCycleDay = 9

    private val _drivingEvent = MutableLiveData<Long>()
    val drivingEventLiveData: LiveData<Long> = _drivingEvent
    private val _drivingLimit = MutableLiveData<Long>()
    val drivingLimitLiveData: LiveData<Long> = _drivingLimit
    private val _onEvent = MutableLiveData<Long>()
    val onEventLiveData: LiveData<Long> = _onEvent
    private val _dutyCycleEvent = MutableLiveData<Long>()
    val dutyCycleEventLiveData: LiveData<Long> = _dutyCycleEvent

    private val _onEventWarning = MutableLiveData<Boolean>()
    val onEventWarningLiveData: LiveData<Boolean> = _onEventWarning
    private val _dutyCycleWarning = MutableLiveData<Boolean>()
    val dutyCycleWarningLiveData: LiveData<Boolean> = _dutyCycleWarning

    fun calculateEvents() {
        val reverseEvents = Storage.eventList.reversed()
        reverseEvents.forEachIndexed { index, event ->
            if (event.endDate == null) {
                event.endDate =
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                event.endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            event.calculateDuration()
            val duration = event.durationInMillis / 1000 / 60
            when {
                event.getCode() == "D" -> {
                    if (index == 0) {
                        onDutyBreakInMinutes -= duration.toInt()
                    }
                    onDutyWindowMinutes -= duration.toInt()
                    onDutyCycleMinutes -= duration.toInt()
                    drivingDutyMinutes -= duration.toInt()
                    if (duration >= 480) {
                        needThiryMinuteBreak = true
                    }
                }
                event.getCode() == "On" -> {
                    onDutyWindowMinutes -= duration.toInt()
                    onDutyCycleMinutes -= duration.toInt()
                    if (duration > 30) {
                        needThiryMinuteBreak = false
                    }
                }
                event.getCode() == "Off" -> {
                    val workingTime = if (duration > 0) duration else (-1 * duration)
                    if ((index + 1) < reverseEvents.size && reverseEvents[index + 1].eventCode == "Off") {
                        needResetCycleMinutes -= workingTime
                    } else {
                        resetNeedOnDutyCycleMinutes()
                    }

                    if (needResetCycleMinutes <= 0) {
                        sendResetCycle()
                    } else if (needResetCycleMinutes <= 1440) {
                        resetOnDutyWindowMinutes()
                    }
                    sleeperBethMinutes -= duration.toInt()
                    onDutyCycleMinutes -= duration.toInt()
                }
                event.getCode() == "SB" -> {
                    val workingTime = if (duration > 0) duration else (-1 * duration)
                    if ((index + 1) < reverseEvents.size && reverseEvents[index + 1].eventCode == "Off") {
                        needResetCycleMinutes -= workingTime
                    } else {
                        resetNeedOnDutyCycleMinutes()
                    }

                    if (needResetCycleMinutes <= 0) {
                        sendResetCycle()
                    } else if (needResetCycleMinutes <= 1440) {
                        resetOnDutyWindowMinutes()
                    }
                }
            }

            if (onDutyCycleMinutes <= 0) {
                _dutyCycleWarning.postValue(true)
                //  showMessage(title: "Warning", messageBody: "You on duty more 70 hour", statusColor: .warning)
            }

            if (onDutyWindowMinutes <= 0) {
                _onEventWarning.postValue(true)
                //  showMessage(title: "Warning", messageBody: "You continuously on duty more 14 hour", statusColor: .warning)
            }
        }

        _drivingEvent.postValue(onDutyBreakInMinutes.toLong() * 60 * 1000)
        _onEvent.postValue(onDutyWindowMinutes.toLong() * 60 * 1000)
        _dutyCycleEvent.postValue(onDutyCycleMinutes.toLong() * 60 * 1000)
        _drivingLimit.postValue(drivingDutyMinutes.toLong() * 60 * 1000)
    }


    // 8
    fun startCountDrivingEvent(onDutyBreakInMillis: Long) {
        _drivingEvent.postValue(onDutyBreakInMillis)
    }

    // 14
    fun startCountOnEvent(onDutyWindowMinutes: Long) {
        _onEvent.postValue(onDutyWindowMinutes)
    }

    // 70
    fun startCountDutyCycleEvent(onDutyCycleMinutes: Long) {
        _dutyCycleEvent.postValue(onDutyCycleMinutes)
    }

    // 11
    fun startCountDrivingLimit(drivingDutyMinutes: Long) {
        _drivingLimit.postValue(drivingDutyMinutes)
    }

    private fun resetMinutes() {
        onDutyCycleMinutes = 4200
        onDutyWindowMinutes = 840
        sleeperBethMinutes = 600
        onDutyBreakInMinutes = 480
        drivingDutyMinutes = 660
    }

    private fun resetOnDutyWindowMinutes() {
        onDutyWindowMinutes = 840
    }

    private fun resetNeedOnDutyCycleMinutes() {
        needResetCycleMinutes = 2040
    }

    private fun resetDrivingDutyCycleMinutes() {
        drivingDutyMinutes = 660
    }

    private fun sendResetCycle() {
        val event = Event()
        val timezone = Timezone.findByName(timezone = sp.getString(USER_TIMEZONE, null) ?: "")
        event.date = Date().formatToServerDateDefaults(timezone = timezone)
        event.time = Date().formatToServerTimeDefaults(timezone = timezone)
        event.eventType = EventInsertType.CYCLE_RESET.type
        event.eventCode = EventCode.CYCLE_RESET.code
        eventViewModel.insertEvent(e = event)
    }
}