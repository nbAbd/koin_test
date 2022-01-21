package com.pieaksoft.event.consumer.android.events

import com.pieaksoft.event.consumer.android.db.AppDataBase
import com.pieaksoft.event.consumer.android.model.Event
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Result
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.network.ServiceApi

class EventsRepositoryImpl(private val serviceApi: ServiceApi, val db: AppDataBase) : EventsRepository {
    override suspend fun insertEvent(event: Event): Result<Event, Exception> {
        return try {
            val response = serviceApi.insertEvent(event)
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun insertEventToDB(event: Event) {
        event.isSyncWithServer = false
        db.getAppDao().saveEvent(event)
    }

    override suspend fun certifyEvent(event: Event): Result<Event, Exception> {
        return try {
            val response = serviceApi.certifyEvent(event)
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun getEventList(): Result<List<Event>, Exception> {
        return try {
            val response = serviceApi.getEventList()
            Success(response)
        } catch (e: Exception) {
            Failure(e)
        }
    }

    override suspend fun getEventListFromDB(): List<Event> {
        return db.getAppDao().getAllEvents()
    }

    override suspend fun saveEventListToDB(eventList: List<Event>) {
        eventList.forEach { event ->
            event.isSyncWithServer = true
            db.getAppDao().saveEvent(event)
        }
    }

    override suspend fun deleteAllEvents() {
        db.getAppDao().deleteEvents()
    }
}