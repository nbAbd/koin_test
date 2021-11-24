package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import com.pieaksoft.event.consumer.android.model.EventCalculationModel
import com.pieaksoft.event.consumer.android.ui.base.BaseVM
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent

class EventsCalculationVM(val app: Application) : BaseVM(app) {

    private val _breakInEvent = SingleLiveEvent<EventCalculationModel>()
    val breakInEventLiveData: LiveData<EventCalculationModel> = _breakInEvent

    var breakIn: EventCalculationModel? = null


    var breakInTimer: CountDownTimer? = null

    fun initBreakIn() {
        breakIn = EventCalculationModel()
        breakIn?.totalLimit = 8 * 60 * 60 * 1000
        breakIn?.remainMillis = 8 * 60 * 60 * 1000
    }

    fun startCountBreakIn() {
        breakIn?.let {
            breakInTimer = object : CountDownTimer(it.remainMillis, 60000) {
                override fun onTick(millisUntilFinished: Long) {
                    it.remainMillis = millisUntilFinished
                    _breakInEvent.postValue(it)
                }

                override fun onFinish() {
                    it.remainMillis = 0
                    _breakInEvent.postValue(it)
                }

            }.start()
        }
    }

    fun stopCountBreakIn() {
        breakInTimer?.cancel()
    }


}