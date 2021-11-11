package com.pieaksoft.event.consumer.android.events

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.pieaksoft.event.consumer.android.model.*
import com.pieaksoft.event.consumer.android.ui.base.BaseVM
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import com.pieaksoft.event.consumer.android.utils.Storage
import kotlinx.coroutines.launch

class EventsVM(val app: Application, private val repo: EventsRepo) : BaseVM(app) {
    private val eventObserver = SingleLiveEvent<Event>()
    val eventLiveData: LiveData<Event> = eventObserver

    private val _eventCertObserver = SingleLiveEvent<Event>()
    val eventCertLiveData: LiveData<Event> = _eventCertObserver

    private val eventListObserver = SingleLiveEvent<List<Event>>()
    val eventListLiveData: LiveData<List<Event>> = eventListObserver

    val eventGroupByDateObservable by lazy {
        SingleLiveEvent<Map<String, List<Event>>>()
    }

    val certificationNeedListObservable by lazy {
        SingleLiveEvent<List<Event>>()
    }

    fun insertEvent(event: Event) {
        _progress.postValue(true)
        launch {
            when (val response = repo.insertEvent(event)) {
                is Success -> {
                    response.data.let {
                        eventObserver.postValue(it)
                        _progress.postValue(false)
                    }
                }
                is Failure -> {
                    _error.value = response.error
                    _progress.postValue(false)
                }
            }
        }
    }

    fun certifyEvent(date: String, event: Event) {
        event.certification = Certification(date, "CERTIFIED")
        _progress.postValue(true)
        Log.e("test_logcert","cert = "+ event)
        launch {
            when (val response = repo.certifyEvent(event)) {
                is Success -> {
                    response.data.let {
                        _eventCertObserver.postValue(it)
                        _progress.postValue(false)
                    }
                }
                is Failure -> {
                    _error.value = response.error

                    Log.e("test_error","test error = "+ response.error)
                    _progress.postValue(false)
                }
            }
        }
    }

    fun getEventList() {
        launch {
            when (val response = repo.getEventList()) {
                is Success -> {
                    response.data.let { list ->
                        Log.e("test_log22","test = "+list)
                        eventListObserver.postValue(list)
                        Storage.eventList = list.filter { it.eventType == EventInsertType.statusChange.type }
                        Storage.eventListGroupByDate = Storage.eventList.groupBy { it.date ?: "" }
                        eventGroupByDateObservable.postValue(getEventsGroupByDate())
                        calculateEndTime()
                        checkCertifications()
                    }
                }
                is Failure -> {
                    Log.e("test_log22","test error = "+response.error)
                    _error.value = response.error
                }
            }
        }
    }

    fun getEventsGroupByDate(): Map<String, List<Event>> {
        return Storage.eventListGroupByDate
    }

    private fun checkCertifications() {
        val eventNeedCertList = mutableListOf<Event>()
        for (event in Storage.eventList) {
            if (event.certifyDate != null && event.certifyDate?.isEmpty() == true && event.eventType == EventInsertType.statusChange.type) {
                eventNeedCertList.add(event)
            }
        }
        certificationNeedListObservable.postValue(eventNeedCertList)
    }

    private fun calculateEndTime() {
        Storage.eventList.forEachIndexed { index, event ->
            if (index < Storage.eventList.size - 1) {
                event.endDate = Storage.eventList[index + 1].date
                event.endTime = Storage.eventList[index + 1].time
            }
        }
    }
}