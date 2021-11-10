package com.pieaksoft.event.consumer.android.events

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.ui.base.BaseVM
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import com.pieaksoft.event.consumer.android.utils.Storage
import kotlinx.coroutines.launch

class EventsVM(val app: Application, private val repo: EventsRepo) : BaseVM(app) {
    private val eventObserver = SingleLiveEvent<Event>()
    val eventLiveData: LiveData<Event> = eventObserver

    private val eventListObserver = SingleLiveEvent<List<Event>>()
    val eventListLiveData: LiveData<List<Event>> = eventListObserver

    val eventGroupByDateObservable by lazy {
        SingleLiveEvent<Map<String, List<Event>>>()
    }

    val certificationNeedListObservable by lazy {
        SingleLiveEvent<List<Event>>()
    }

    fun insertEvent(event: Event) {
        launch {
            when (val response = repo.insertEvent(event)) {
                is Success -> {
                    response.data.let {
                        eventObserver.postValue(it)
                    }
                }
                is Failure -> {
                    _error.value = response.error
                }
            }
        }
    }

    fun getEventList() {
        launch {
            when (val response = repo.getEventList()) {
                is Success -> {
                    response.data.let { list ->
                        eventListObserver.postValue(list)
                        Storage.eventList = list
                        Storage.eventListGroupByDate = Storage.eventList.groupBy { it.date ?: "" }
                        eventGroupByDateObservable.postValue(getEventsGroupByDate())
                    }
                }
                is Failure -> {
                    _error.value = response.error
                }
            }
        }
    }

    fun getEventsGroupByDate(): Map<String, List<Event>> {
        return Storage.eventListGroupByDate
    }

    fun checkCertifications() {
        val eventNeedCertList = mutableListOf<Event>()
        for (event in Storage.eventList) {
            if (event.certification == null) {
                eventNeedCertList.add(event)
            }
        }
        certificationNeedListObservable.postValue(eventNeedCertList)
    }

    fun calculateEndTime() {
        Storage.eventList.forEachIndexed { index, event ->
            if (index < Storage.eventList.size - 1) {
                event.endDate = Storage.eventList[index + 1].date
                event.endTime = Storage.eventList[index + 1].time
            }
        }
    }
}