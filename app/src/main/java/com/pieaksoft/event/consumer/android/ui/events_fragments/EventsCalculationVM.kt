package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.app.Application
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.EventCalculationModel
import com.pieaksoft.event.consumer.android.ui.base.BaseVM
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import com.pieaksoft.event.consumer.android.utils.Storage
import com.pieaksoft.event.consumer.android.utils.getCode
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import kotlin.math.abs

const val on_Duty_Cycle_Minutes = 4200
const val on_Duty_Window_Minutes = 840
const val sleeper_Beth_Minutes = 600
const val on_Duty_Break_In_Minutes = 480

class EventsCalculationVM(val app: Application) : BaseVM(app) {


    private var onDutyCycleMinutes = 4200 // per minute 70 hour
    private var onDutyWindowMinutes = 840 // per minute 14 hour
    private var sleeperBethMinutes = 600 // per minute 10 hour
    private var onDutyBreakInMinutes = 480 // per minute 8 hour
    private var drivingDutyMinutes = 660 // per minute 11 hour
    private var needResetCycleMinutes: Long = 2040 // per minute 34 hour
    private var needThiryMinuteBreak: Boolean = false
    private var needResetCycleDay = 9

    private val _drivingEvent = SingleLiveEvent<Long>()
    val drivingEventLiveData: LiveData<Long> = _drivingEvent
    private val _drivingLimit = SingleLiveEvent<Long>()
    val drivingLimitLiveData: LiveData<Long> = _drivingLimit
    private val _onEvent = SingleLiveEvent<Long>()
    val onEventLiveData: LiveData<Long> = _onEvent
    private val _dutyCycleEvent = SingleLiveEvent<Long>()
    val dutyCycleEventLiveData: LiveData<Long> = _dutyCycleEvent

    private val _onEventWarning = SingleLiveEvent<Boolean>()
    val onEventWarningLiveData: LiveData<Boolean> = _onEventWarning
    private val _dutyCycleWarning = SingleLiveEvent<Boolean>()
    val dutyCycleWarningLiveData: LiveData<Boolean> = _dutyCycleWarning


    private var drivingTimer = Handler(Looper.getMainLooper())
    private val driverCountTask: Runnable by lazy {
        kotlinx.coroutines.Runnable {
            val remainMillis = (onDutyBreakInMinutes) * 60 * 1000
            _drivingEvent.postValue(remainMillis.toLong())
            drivingTimer.removeCallbacks(driverCountTask)
            drivingTimer.postDelayed(driverCountTask, 60000)
            onDutyBreakInMinutes -= 1
        }
    }

    private var onTimer = Handler(Looper.getMainLooper())
    private val onCountTask: Runnable by lazy {
        kotlinx.coroutines.Runnable {
            val remainMillis = (onDutyWindowMinutes) * 60 * 1000
            _onEvent.postValue(remainMillis.toLong())
            onTimer.removeCallbacks(onCountTask)
            onTimer.postDelayed(onCountTask, 60000)
            onDutyWindowMinutes -= 1
        }
    }

    private var dutyCycleTimer = Handler(Looper.getMainLooper())
    private val dutyCycleCountTask: Runnable by lazy {
        kotlinx.coroutines.Runnable {
            val remainMillis = (onDutyCycleMinutes) * 60 * 1000
            _dutyCycleEvent.postValue(remainMillis.toLong())
            dutyCycleTimer.removeCallbacks(dutyCycleCountTask)
            dutyCycleTimer.postDelayed(dutyCycleCountTask, 60000)
            onDutyCycleMinutes -= 1
        }
    }

    private var drivingLimitTimer = Handler(Looper.getMainLooper())
    private val drivingLimitCountTask: Runnable by lazy {
        kotlinx.coroutines.Runnable {
            val remainMillis = (drivingDutyMinutes) * 60 * 1000
            _dutyCycleEvent.postValue(remainMillis.toLong())
            drivingLimitTimer.removeCallbacks(drivingLimitCountTask)
            drivingLimitTimer.postDelayed(drivingLimitCountTask, 60000)
            drivingDutyMinutes -= 1
        }
    }

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
                    var workingTime = if (duration > 0) duration else (-1 * duration)
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
                //  showMessage(title: "Warning", messageBody: "You onduty more 70 hour", statusColor: .warning)
            }

            if (onDutyWindowMinutes <= 0) {
                _onEventWarning.postValue(true)
                //  showMessage(title: "Warning", messageBody: "You continously onduty more 14 hour", statusColor: .warning)
            }
        }

        _drivingEvent.postValue(onDutyBreakInMinutes.toLong() * 60 * 1000)
        _onEvent.postValue(onDutyWindowMinutes.toLong() * 60 * 1000)
        _dutyCycleEvent.postValue(onDutyCycleMinutes.toLong() * 60 * 1000)
        _drivingLimit.postValue(drivingDutyMinutes.toLong() * 60 * 1000)
    }

    fun startCountDrivingEvent() {
        drivingTimer.postDelayed(driverCountTask, 60000)
    }

    fun startCountOnEvent() {
        onTimer.postDelayed(onCountTask, 60000)
    }

    fun startCountDutyCycleEvent() {
        dutyCycleTimer.postDelayed(dutyCycleCountTask, 60000)
    }

    fun startCountDrivingLimit() {
        drivingLimitTimer.postDelayed(drivingLimitCountTask, 60000)
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
       // var event = Event()
//        event.eventType = . CYCLE_RESET
//        event.eventCode = . CYCLE_RESET
//        viewModel.postEvent(event: event)
    }



}