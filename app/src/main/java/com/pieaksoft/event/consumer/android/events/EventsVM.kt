package com.pieaksoft.event.consumer.android.events

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.pieaksoft.event.consumer.android.db.AppDataBase
import com.pieaksoft.event.consumer.android.model.*
import com.pieaksoft.event.consumer.android.ui.base.BaseVM
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import com.pieaksoft.event.consumer.android.utils.Storage
import com.pieaksoft.event.consumer.android.utils.getCode
import com.pieaksoft.event.consumer.android.utils.hmsTimeFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class EventsVM(val app: Application, private val repo: EventsRepo) : BaseVM(app) {
    private val eventObserver = SingleLiveEvent<Event>()
    val eventLiveData: LiveData<Event> = eventObserver

    private val _eventCertObserver = SingleLiveEvent<Event>()
    val eventCertLiveData: LiveData<Event> = _eventCertObserver

    private val eventListObserver = SingleLiveEvent<List<Event>>()
    val eventListLiveData: LiveData<List<Event>> = eventListObserver

    val eventGroupByDateObservable by lazy {
        SingleLiveEvent<Map<String, List<Event>>>()
    }

    val certificationNeedListObservable by lazy {
        SingleLiveEvent<List<Event>>()
    }

    fun insertEvent(event: Event) {
        _progress.postValue(true)
        launch {
            when (val response = repo.insertEvent(event)) {
                is Success -> {
                    response.data.let {
                        eventObserver.postValue(it)
                        _progress.postValue(false)
                    }
                }
                is Failure -> {
                    _error.value = response.error
                    _progress.postValue(false)
                }
            }
        }
    }

    fun certifyEvent(date: String, event: Event) {
        event.certification = Certification(date, "CERTIFIED")
        _progress.postValue(true)
        launch {
            when (val response = repo.certifyEvent(event)) {
                is Success -> {
                    response.data.let {
                        _eventCertObserver.postValue(it)
                        _progress.postValue(false)
                    }
                }
                is Failure -> {
                    _error.value = response.error

                    Log.e("test_error", "test error = " + response.error)
                    _progress.postValue(false)
                }
            }
        }
    }

    fun getEventList() {
        launch {
            if (Storage.isNetworkEnable) {
                when (val response = repo.getEventList()) {
                    is Success -> {
                        response.data.let { list ->
                            handleEventsList(list)
                            launch(Dispatchers.IO) {
                                repo.deleteAllEvents()
                                repo.saveEventListToDB(Storage.eventList)
                                Log.e("test_log", "test from db = " + repo.getEventListFromDB())
                            }
                        }
                    }
                    is Failure -> {
                        _error.value = response.error
                    }
                }
            } else {
                launch(Dispatchers.IO) {
                    val events = repo.getEventListFromDB()
                    handleEventsList(events)
                    Log.e("test_log", "test get events form DB = $events")
                }
            }
        }
    }

    private fun handleEventsList(list: List<Event>) {
        eventListObserver.postValue(list)
        Storage.eventList =
            list.filter { it.eventType == EventInsertType.statusChange.type }
        checkCertifications()
        Storage.eventListGroupByDate = calculateEvents()
        eventGroupByDateObservable.postValue(getEventsGroupByDate())
    }

    fun getEventsGroupByDate(): Map<String, List<Event>> {
        return Storage.eventListGroupByDate
    }


    private fun calculateEvents(): Map<String, List<Event>> {
        calculateEndTime()
        val calculateList: MutableList<Event> = mutableListOf()
        Storage.eventList.forEachIndexed { index, event ->
            Log.e("test_log", "test date before parse = " + event.date)
            val startDate = LocalDate.parse(event.date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val endDate = LocalDate.parse(event.endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val dbd = ChronoUnit.DAYS.between(startDate, endDate).toInt()
            if (dbd == 0) {
                calculateList.add(event)
            } else if (dbd > 0) {
                val mEvent = event.copy(endDate = event.date, endTime = "25:00")
                calculateList.add(mEvent)
                for (i in 1..dbd) {
                    val date = startDate.plusDays(i.toLong())
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    val endDate = startDate.plusDays(i.toLong())
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
                                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                            mEvent.endTime =
                                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                        }
                    }
                    calculateList.add(mEvent)
                }
            }
        }
        var map = calculateList.groupBy { it.date ?: "" }.toMutableMap()
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
        for (i in 1..7) {
            val date =
                currentDay.minusDays(i.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            Storage.eventListMock.add(date)
        }
        Log.e("test_dates", " test dates = " + Storage.eventListMock)
    }

    private fun checkCertifications() {
        val eventNeedCertList = mutableListOf<Event>()
        for (event in Storage.eventList) {
            if (event.certifyDate != null && event.certifyDate?.isEmpty() == true && event.eventType == EventInsertType.statusChange.type) {
                eventNeedCertList.add(event)
            }
        }
        certificationNeedListObservable.postValue(eventNeedCertList)
    }

    private fun calculateEndTime() {
        Storage.eventList.forEachIndexed { index, event ->
            if (index < Storage.eventList.size - 1) {
                event.endDate = Storage.eventList[index + 1].date
                event.endTime = Storage.eventList[index + 1].time
            } else {
                event.endDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                event.endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
            }

            event.calculateDuration()
            Log.e("test_log5", "test duration = " + hmsTimeFormatter(event.durationInMillis))
        }
    }
}