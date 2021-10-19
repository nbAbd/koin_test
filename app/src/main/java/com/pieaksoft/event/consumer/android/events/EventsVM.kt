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

    fun insertEvent(event: Event) {
        Log.e("test_log", "test = " + event)
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
                    response.data.let {
                        eventListObserver.postValue(it)
                        Storage.eventList = it
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
        return Storage.eventList.groupBy { it.date ?: "" }
    }
}