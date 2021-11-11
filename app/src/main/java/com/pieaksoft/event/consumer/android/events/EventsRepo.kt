package com.pieaksoft.event.consumer.android.events

import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.Result

interface EventsRepo {
    suspend fun insertEvent(event: Event): Result<Event, Exception>
    suspend fun certifyEvent(event: Event): Result<Event, Exception>
    suspend fun getEventList(): Result<List<Event>, Exception>
}