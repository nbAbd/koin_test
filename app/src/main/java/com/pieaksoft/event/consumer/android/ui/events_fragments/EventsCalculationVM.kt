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

    var lastEvent = Storage.eventList.last()

    var breakIn: EventCalculationModel? = null
    var drivingEvent: EventCalculationModel? = null


    var drivingTimer: CountDownTimer? = null

    fun initBreakIn() {
        breakIn = EventCalculationModel()
        breakIn?.totalLimit = 8 * 60 * 60 * 1000
        breakIn?.remainMillis = 8 * 60 * 60 * 1000
    }

    fun initDrivingEvent(){
        if(lastEvent.getCode() == "D"){
            lastEvent.endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            lastEvent.endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            lastEvent.calculateDuration()
            drivingEvent = EventCalculationModel()
            drivingEvent?.totalLimit = 8 * 60 * 60 * 1000
            drivingEvent?.remainMillis = ((drivingEvent?.totalLimit?:0) - lastEvent.durationInMillis)
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

    fun stopCountBreakIn() {
        drivingTimer?.cancel()
    }


}