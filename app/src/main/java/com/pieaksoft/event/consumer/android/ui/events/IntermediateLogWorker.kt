package com.pieaksoft.event.consumer.android.ui.events

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.pieaksoft.event.consumer.android.events.EventsRepository
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.utils.isNetworkAvailable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class IntermediateLogWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val repo: EventsRepository by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val event = Gson().fromJson(
            inputData.getString(IntermediateLogHandler.EVENT_CODE),
            Event::class.java
        )
        if (applicationContext.isNetworkAvailable()) {
            repo.insertEvent(event)
        } else repo.insertEventToDB(event)

        return@withContext Result.success()
    }
}