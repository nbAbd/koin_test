package com.pieaksoft.event.consumer.android.events

import androidx.lifecycle.LiveData
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.ui.base.BaseVM
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class EventsVM(private val repo: EventsRepo) : BaseVM() {
    private val eventObserver = SingleLiveEvent<Event>()
    val eventLiveData: LiveData<Event> = eventObserver

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
}