package com.pieaksoft.event.consumer.android.events

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pieaksoft.event.consumer.android.model.*
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.utils.Storage
import com.pieaksoft.event.consumer.android.utils.hmsTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.emptyList
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.collections.forEachIndexed
import kotlin.collections.groupBy
import kotlin.collections.isNullOrEmpty
import kotlin.collections.mutableListOf
import kotlin.collections.set
import kotlin.collections.toMutableMap
import kotlin.collections.toSortedMap

class EventViewModel(app: Application, private val repository: EventsRepository) :
    BaseViewModel(app) {
    companion object {
        const val TAG = "EventViewModel ->"
        const val STATUS_CERTIFIED = "CERTIFIED"
        const val DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd"
        const val TIME_FORMAT_HH_MM = "HH:mm"
    }

    val event = MutableLiveData<Event>()
    val localEvent = MutableLiveData<Event>()
    val certifiedEvent = MutableLiveData<Event>()
    val certifiedDate = MutableLiveData<String>()
    val eventList = MutableLiveData<List<Event>>()
    val eventListByDate = MutableLiveData<Map<String, List<Event>>>()
    val eventListRequiresCertification = MutableLiveData<List<Event>>()

    fun insertEvent(e: Event) {
        showProgress()
        launch {
            if (isNetworkAvailable) {
                val result = withContext(Dispatchers.IO) { repository.insertEvent(event = e) }

                hideProgress()

                when (result) {
                    is Success -> result.data.let { event.value = it }
                    is Failure -> _error.value = result.error
                }
            } else {
                // insert to Room
                withContext(Dispatchers.IO) { repository.insertEventToDB(event = e) }

                hideProgress()
                localEvent.value = e
            }
        }
    }


    fun checkNotSyncedEvents() {
        launch(context = Dispatchers.IO) {
            repository.getEventListFromDB().forEach {
                if (it.isSyncWithServer == false) {
                    insertEvent(it)
                    Log.d(TAG, "\nNot synced event: $event")
                }
            }
        }
    }

    fun certifyEvent(date: String, event: Event) {
        event.certification = Certification(date = date, status = STATUS_CERTIFIED)
        showProgress()
        launch {
            val result = withContext(Dispatchers.IO) { repository.certifyEvent(event = event) }

            hideProgress()

            when (result) {
                is Success -> result.data.let {
                    certifiedEvent.value = it
                    certifiedDate.value = date
                }
                is Failure -> _error.value = result.error
            }
        }
    }

    fun getEventList() {
        // show progress during operation on Main (default) thread
        showProgress()

        // launch the Coroutine
        launch {
            if (isNetworkAvailable) {
                // Switching from MAIN to IO thread for API operation
                val result = withContext(Dispatchers.IO) { repository.getEventList() }

                // Hide progress once the operation is done on the MAIN (default) thread
                hideProgress()
                when (result) {
                    is Success -> {
                        handleEvents(events = result.data)
                        withContext(Dispatchers.IO) {
                            repository.deleteAllEvents()
                            repository.saveEventListToDB(eventList = Storage.eventList)
                        }
                    }
                    is Failure -> _error.value = result.error
                }
            } else {
                // Switching from MAIN to IO thread for Database operation
                val localEvents = withContext(Dispatchers.IO) { repository.getEventListFromDB() }

                // Hide progress once the operation is done on the MAIN (default) thread
                hideProgress()
                handleEvents(events = localEvents)
            }
        }
    }

    private fun handleEvents(events: List<Event>) {
        Storage.eventList = events.filter { it.eventType == EventInsertType.statusChange.type }
        Storage.eventListGroupByDate = calculateEvents()
        eventList.value = events
        eventListByDate.value = Storage.eventListGroupByDate
        eventListRequiresCertification.value =
            Storage.eventList.filter { !it.certifyDate.isNullOrEmpty() }
    }

    fun getEventsGroupByDate(): Map<String, List<Event>> {
        return Storage.eventListGroupByDate
    }

    private fun calculateEvents(): Map<String, List<Event>> {
        calculateEndTime()
        val calculateList: MutableList<Event> = mutableListOf()
        Storage.eventList.forEachIndexed { index, event ->
            Log.e(TAG, "test date before parse = " + event.date)
            val startDate = LocalDate.parse(
                event.date, DateTimeFormatter.ofPattern(
                    DATE_FORMAT_YYYY_MM_DD
                )
            )
            val endDate =
                LocalDate.parse(event.endDate, DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD))
            val dbd = ChronoUnit.DAYS.between(startDate, endDate).toInt()
            if (dbd == 0) {
                calculateList.add(event)
            } else if (dbd > 0) {
                val mEvent = event.copy(endDate = event.date, endTime = "25:00")
                calculateList.add(mEvent)
                for (i in 1..dbd) {
                    val date = startDate.plusDays(i.toLong())
                        .format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD))
                    val endDate = startDate.plusDays(i.toLong())
                        .format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD))
                    val mEvent = event.copy(
                        date = date,
                        endDate = endDate,
                        time = "00:00",
                        endTime = "25:00"
                    )
                    if (i == dbd) {
                        if (index < Storage.eventList.size - 1) {
                            mEvent.endDate = Storage.eventList[index + 1].date
                            mEvent.endTime = Storage.eventList[index + 1].time
                        } else {
                            mEvent.endDate =
                                LocalDate.now().format(
                                    DateTimeFormatter.ofPattern(
                                        DATE_FORMAT_YYYY_MM_DD
                                    )
                                )
                            mEvent.endTime =
                                LocalTime.now()
                                    .format(DateTimeFormatter.ofPattern(TIME_FORMAT_HH_MM))
                        }
                    }
                    calculateList.add(mEvent)
                }
            }
        }
        val map = calculateList.groupBy { it.date ?: "" }.toMutableMap()
        for (i in Storage.eventListMock) {
            if (!map.containsKey(i)) {
                map[i] = emptyList()
            }
        }
        return map.toSortedMap()
    }

    fun setEventsMock() {
        val currentDay = LocalDate.now()
        Storage.eventList.groupBy { it.date ?: "" }
        Storage.eventListMock.add(
            currentDay.format(
                DateTimeFormatter.ofPattern(
                    DATE_FORMAT_YYYY_MM_DD
                )
            )
        )
        for (i in 1..7) {
            val date =
                currentDay.minusDays(i.toLong())
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD))
            Storage.eventListMock.add(date)
        }
    }

    private fun calculateEndTime() {
        Storage.eventList.forEachIndexed { index, event ->
            if (index < Storage.eventList.size - 1) {
                event.endDate = Storage.eventList[index + 1].date
                event.endTime = Storage.eventList[index + 1].time
            } else {
                event.endDate =
                    LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD))
                event.endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            }

            event.calculateDuration()
            Log.e(TAG, "test duration = " + hmsTimeFormatter(event.durationInMillis))
        }
    }
}