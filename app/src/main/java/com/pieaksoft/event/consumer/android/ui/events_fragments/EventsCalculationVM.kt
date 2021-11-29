package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.app.Application
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
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

    private val _drivingEvent = SingleLiveEvent<Long>()
    val drivingEventLiveData: LiveData<Long> = _drivingEvent
    private val _onEvent = SingleLiveEvent<Long>()
    val onEventEventLiveData: LiveData<Long> = _onEvent

    var lastEvent = Storage.eventList.lastOrNull()


    var drivingEvent: EventCalculationModel? = null
    var onEvent: EventCalculationModel? = null


    var drivingTimer: CountDownTimer? = null
    var onTimer: CountDownTimer? = null


    fun calculateEvents() {
        Storage.eventList.reversed().forEachIndexed { index, event ->
            if (event.endDate == null) {
                event.endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                event.endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            }
            event.calculateDuration()
            val min = event.durationInMillis / 1000 / 60
            when {
                event.getCode() == "D" -> {
                    Log.e("test_log123","test = "+ onDutyBreakInMinutes)
                    onDutyBreakInMinutes -= min.toInt()
                    onDutyWindowMinutes -= min.toInt()
                    Log.e("test_log123","test22 = "+ onDutyBreakInMinutes)
                }
                event.getCode() == "On" -> {
                    onDutyWindowMinutes -= min.toInt()
                }
                event.getCode() == "SB" -> {
                    sleeperBethMinutes -= min.toInt()
                }
                event.getCode() == "Off" -> {

                }
            }
            Log.e("test_log", "test minutes = $min")
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

    fun stopCountBreakIn() {
        drivingTimer?.cancel()
    }


}