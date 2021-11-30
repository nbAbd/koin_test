package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.app.Application
import android.os.CountDownTimer
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
    private var needResetCycleMinutes: Long = 2040 // per minute 34 hour
    private var needResetCycleDay = 9

    private val _drivingEvent = SingleLiveEvent<Long>()
    val drivingEventLiveData: LiveData<Long> = _drivingEvent
    private val _onEvent = SingleLiveEvent<Long>()
    val onEventLiveData: LiveData<Long> = _onEvent
    private val _dutyCycleEvent = SingleLiveEvent<Long>()
    val dutyCycleEventLiveData: LiveData<Long> = _dutyCycleEvent


    var drivingTimer: CountDownTimer? = null
    var onTimer: CountDownTimer? = null
    var dutyCycleTimer: CountDownTimer? = null


    fun calculateEvents() {
        val reverseEvents = Storage.eventList.reversed()
        reverseEvents.forEachIndexed { index, event ->
            if (event.endDate == null) {
                event.endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
                }
                event.getCode() == "On" -> {
                    onDutyWindowMinutes -= duration.toInt()
                    onDutyCycleMinutes -= duration.toInt()
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
                    var workingTime = if(duration > 0)  duration else (-1 * duration)
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
              //  showMessage(title: "Warning", messageBody: "You onduty more 70 hour", statusColor: .warning)
            }

            if (onDutyWindowMinutes <= 0) {
              //  showMessage(title: "Warning", messageBody: "You continously onduty more 14 hour", statusColor: .warning)
            }
        }
    }

    fun startCountDrivingEvent() {
        val remainMillis = (onDutyBreakInMinutes) * 60 * 1000
        drivingTimer = object : CountDownTimer(remainMillis.toLong(), 60000) {
            override fun onTick(millisUntilFinished: Long) {
                _drivingEvent.postValue(millisUntilFinished)
            }

            override fun onFinish() {
                _drivingEvent.postValue(0)
            }

        }.start()
    }

    fun startCountOnEvent() {
        val remainMillis = (onDutyWindowMinutes) * 60 * 1000
        onTimer = object : CountDownTimer(remainMillis.toLong(), 60000) {
            override fun onTick(millisUntilFinished: Long) {
                _onEvent.postValue(millisUntilFinished)
            }

            override fun onFinish() {
                _onEvent.postValue(0)
            }

        }.start()
    }

    fun startCountDutyCycleEvent() {
        val remainMillis = (onDutyCycleMinutes) * 60 * 1000
        dutyCycleTimer = object : CountDownTimer(remainMillis.toLong(), 60000) {
            override fun onTick(millisUntilFinished: Long) {
                _dutyCycleEvent.postValue(millisUntilFinished)
            }

            override fun onFinish() {
                _dutyCycleEvent.postValue(0)
            }

        }.start()
    }


    private fun resetMinutes() {
        onDutyCycleMinutes = 4200
        onDutyWindowMinutes = 840
        sleeperBethMinutes = 600
        onDutyBreakInMinutes = 480
    }

    private fun resetOnDutyWindowMinutes() {
        onDutyWindowMinutes = 840
    }

    private fun resetNeedOnDutyCycleMinutes() {
        needResetCycleMinutes = 2040
    }

    private fun sendResetCycle() {
        var event = Event()
//        event.eventType = . CYCLE_RESET
//        event.eventCode = . CYCLE_RESET
//        viewModel.postEvent(event: event)
    }


    fun stopCountBreakIn() {
        drivingTimer?.cancel()
    }


}