package com.pieaksoft.event.consumer.android.events

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
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
import java.util.*
import kotlin.collections.set

class EventViewModel(app: Application, private val repository: EventsRepository) :
    BaseViewModel(app) {
    companion object {
        const val TAG = "EventViewModel ->"
        const val STATUS_CERTIFIED = "CERTIFIED"
        const val DATE_FORMAT_yyyy_MM_dd = "yyyy-MM-dd"
        const val TIME_FORMAT_HH_mm = "HH:mm"
    }

    val certifiedDate = MutableLiveData<String?>()
    val eventList = MutableLiveData<List<Event>>()
    val eventListByDate = MutableLiveData<Map<String, List<Event>>>()
    val eventListRequiresCertification = MutableLiveData<List<Event>>()

    ///////////////////////////////////////////////////////////////////////////
    // Insert event
    ///////////////////////////////////////////////////////////////////////////
    private val _eventInsertCode = MutableLiveData<EventInsertCode?>()
    val eventInsertCode: LiveData<EventInsertCode?> = _eventInsertCode

    private val _eventInsertDate = MutableLiveData<Date?>()
    val eventInsertDate: LiveData<Date?> = _eventInsertDate

    private val _event = MutableLiveData<Event?>(null)
    val event: LiveData<Event?> = _event

    private val _localEvent = MutableLiveData<Event?>(null)
    val localEvent: LiveData<Event?> = _localEvent

    fun insertEvent(e: Event) {
        showProgress()
        launch {
            if (isNetworkAvailable) {
                val result = withContext(Dispatchers.IO) { repository.insertEvent(event = e) }

                hideProgress()

                when (result) {
                    is Success -> result.data.let { _event.value = it }
                    is Failure -> _error.value = result.error
                }
            } else {
                // insert to Room
                withContext(Dispatchers.IO) { repository.insertEventToDB(event = e) }

                hideProgress()
                _localEvent.value = e
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
            if (isNetworkAvailable) {
                val result = withContext(Dispatchers.IO) { repository.certifyEvent(event = event) }

                hideProgress()

                when (result) {
                    is Success -> result.data.let {
                        certifiedDate.value = date
                    }
                    is Failure -> _error.value = result.error
                }
            } else {
                _error.value = Exception("Check internet connection")
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

    fun sendReport(report: Report, callback: () -> Unit) {
        showProgress()
        launch {
            if (isNetworkAvailable) {
                val result = withContext(Dispatchers.IO) { repository.sendReport(report = report) }
                hideProgress()
                when (result) {
                    is Success -> {
                        Log.e(TAG, "sendReport: ${result.data}")
                        callback.invoke()
                    }
                    is Failure -> {
                        _error.value = result.error
                    }
                }
            }
        }
    }

    private fun handleEvents(events: List<Event>) {
        Storage.eventList = events.filter { it.eventType == EventInsertType.statusChange.type }
        Storage.eventListGroupByDate = calculateEvents()
        eventList.value = events
        eventListByDate.value = calculateEvents()
        eventListRequiresCertification.value =
            Storage.eventList.filter { it.certifyDate != null && it.certifyDate!!.isEmpty() }
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
                    DATE_FORMAT_yyyy_MM_dd
                )
            )
            val endDate =
                LocalDate.parse(event.endDate, DateTimeFormatter.ofPattern(DATE_FORMAT_yyyy_MM_dd))
            val dbd = ChronoUnit.DAYS.between(startDate, endDate).toInt()
            if (dbd == 0) {
                calculateList.add(event)
            } else if (dbd > 0) {
                val mEvent = event.copy(endDate = event.date, endTime = "25:00")
                calculateList.add(mEvent)
                for (i in 1..dbd) {
                    val date = startDate.plusDays(i.toLong())
                        .format(DateTimeFormatter.ofPattern(DATE_FORMAT_yyyy_MM_dd))
                    val _endDate = startDate.plusDays(i.toLong())
                        .format(DateTimeFormatter.ofPattern(DATE_FORMAT_yyyy_MM_dd))
                    val copiedEvent = event.copy(
                        date = date,
                        endDate = _endDate,
                        time = "00:00",
                        endTime = "25:00"
                    )
                    if (i == dbd) {
                        if (index < Storage.eventList.size - 1) {
                            copiedEvent.endDate = Storage.eventList[index + 1].date
                            copiedEvent.endTime = Storage.eventList[index + 1].time
                        } else {
                            copiedEvent.endDate =
                                LocalDate.now().format(
                                    DateTimeFormatter.ofPattern(
                                        DATE_FORMAT_yyyy_MM_dd
                                    )
                                )
                            copiedEvent.endTime =
                                LocalTime.now()
                                    .format(DateTimeFormatter.ofPattern(TIME_FORMAT_HH_mm))
                        }
                    }
                    calculateList.add(copiedEvent)
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
                    DATE_FORMAT_yyyy_MM_dd
                )
            )
        )
        for (i in 1..7) {
            val date =
                currentDay.minusDays(i.toLong())
                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT_yyyy_MM_dd))
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
                    LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_yyyy_MM_dd))
                event.endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            }

            event.calculateDuration()
            Log.e(TAG, "test duration = " + hmsTimeFormatter(event.durationInMillis))
        }
    }

    fun setEventInsertCode(code: EventInsertCode) {
        _eventInsertCode.value = code
    }

    fun setEventInsertDate(date: Date) {
        _eventInsertDate.value = date
    }

    ///////////////////////////////////////////////////////////////////////////
    // В случии повторного открытия страницы insert должны сбросить значение
    ///////////////////////////////////////////////////////////////////////////
    fun resetInserting() {
        _event.value = null
        _localEvent.value = null
        _eventInsertCode.value = null
        _eventInsertDate.value = null
    }
}