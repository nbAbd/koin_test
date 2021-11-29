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

class EventsCalculationVM(val app: Application) : BaseVM(app) {

    private val _drivingEvent = SingleLiveEvent<EventCalculationModel>()
    val drivingEventLiveData: LiveData<EventCalculationModel> = _drivingEvent
    private val _onEvent = SingleLiveEvent<EventCalculationModel>()
    val onEventEventLiveData: LiveData<EventCalculationModel> = _onEvent

    var lastEvent = Storage.eventList.lastOrNull()


    var drivingEvent: EventCalculationModel? = null
    var onEvent: EventCalculationModel? = null


    var drivingTimer: CountDownTimer? = null
    var onTimer: CountDownTimer? = null

    fun initDrivingEvent(){
        lastEvent?.let {
            if(it.getCode() == "D"){
                it.endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                it.endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                it.calculateDuration()
                drivingEvent = EventCalculationModel()
                drivingEvent?.totalLimit = 8 * 60 * 60 * 1000
                drivingEvent?.remainMillis = ((drivingEvent?.totalLimit?:0) - it.durationInMillis)
            }
        }
    }

    fun initOnEvent(){
        lastEvent?.let {
            if(it.getCode() == "D"){
                it.endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                it.endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                it.calculateDuration()
                onEvent = EventCalculationModel()
                onEvent?.totalLimit = 14 * 60 * 60 * 1000
                onEvent?.remainMillis = ((onEvent?.totalLimit?:0) - it.durationInMillis)
            }
        }
    }

    fun startCountDrivingEvent() {
        drivingEvent?.let {
            drivingTimer = object : CountDownTimer(it.remainMillis, 60000) {
                override fun onTick(millisUntilFinished: Long) {
                    it.remainMillis = millisUntilFinished
                    _drivingEvent.postValue(it)
                }

                override fun onFinish() {
                    it.remainMillis = 0
                    _drivingEvent.postValue(it)
                }

            }.start()
        }
    }

    fun startCountOnEvent() {
        onEvent?.let {
            onTimer = object : CountDownTimer(it.remainMillis, 60000) {
                override fun onTick(millisUntilFinished: Long) {
                    it.remainMillis = millisUntilFinished
                    _onEvent.postValue(it)
                }

                override fun onFinish() {
                    it.remainMillis = 0
                    _onEvent.postValue(it)
                }

            }.start()
        }
    }

    fun stopCountBreakIn() {
        drivingTimer?.cancel()
    }


}