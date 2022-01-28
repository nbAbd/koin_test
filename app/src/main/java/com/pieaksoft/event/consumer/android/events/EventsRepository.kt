package com.pieaksoft.event.consumer.android.events

import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.Report
import com.pieaksoft.event.consumer.android.model.Result

interface EventsRepository {
    suspend fun insertEvent(event: Event): Result<Event, Exception>
    suspend fun insertEventToDB(event: Event)
    suspend fun certifyEvent(event: Event): Result<Event, Exception>
    suspend fun getEventList(): Result<List<Event>, Exception>
    suspend fun getEventListFromDB(): List<Event>
    suspend fun saveEventListToDB(eventList: List<Event>)
    suspend fun deleteAllEvents()
    suspend fun sendReport(report: Report): Result<Unit, Exception>
}