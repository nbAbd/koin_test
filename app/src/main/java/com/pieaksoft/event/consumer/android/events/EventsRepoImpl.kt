package com.pieaksoft.event.consumer.android.events

import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Result
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.network.ServiceApi

class EventsRepoImpl(private val serviceApi: ServiceApi): EventsRepo {
    override suspend fun insertEvent(event: Event): Result<Event, Exception> {
        return try {
            val response = serviceApi.insertEvent(event)
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }
}