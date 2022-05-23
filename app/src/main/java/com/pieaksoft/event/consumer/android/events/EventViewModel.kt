package com.pieaksoft.event.consumer.android.events

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.enums.*
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.model.event.Certification
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.model.event.containsDate
import com.pieaksoft.event.consumer.android.model.event.isDutyStatusChanged
import com.pieaksoft.event.consumer.android.model.report.Report
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.ui.events.IntermediateLogHandler
import com.pieaksoft.event.consumer.android.utils.EventManager
import com.pieaksoft.event.consumer.android.utils.USER_TIMEZONE
import com.pieaksoft.event.consumer.android.utils.put
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
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

        const val CURRENT_DUTY_STATUS = "current_duty_status"
    }

    var isEditedLastEvent = false

    private var remainingMinutes = 0L

    val certifiedDate = MutableLiveData<String?>()
    val eventList = MutableLiveData<List<Event>>()
    val eventListByDate = MutableLiveData<Map<String, List<Event>>>()
    val eventListRequiresCertification = MutableLiveData<List<Event>>()

    ///////////////////////////////////////////////////////////////////////////
    // Insert event
    ///////////////////////////////////////////////////////////////////////////
    private val _eventInsertCode = MutableLiveData<EventCode?>()
    val eventInsertCode: LiveData<EventCode?> = _eventInsertCode

    private val _eventInsertDate = MutableLiveData<Date?>(null)
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

    fun updateEvent(e: Event) {
        showProgress()
        launch {
            if (isNetworkAvailable) {
                val result = withContext(Dispatchers.IO) { repository.updateEvent(event = e) }

                hideProgress()

                when (result) {
                    is Success -> result.data.let { _event.value = it }
                    is Failure -> _error.value = result.error
                }
            } else {
                // update event in the Room
                withContext(Dispatchers.IO) { repository.insertEventToDB(event = e) }

                hideProgress()
                _localEvent.value = e
            }
        }
    }

    fun certifyEvent(date: String, event: Event) {
        showProgress()
        val certifiedEvent = event.copy(
            certification = Certification(date = date, status = STATUS_CERTIFIED)
        )
        launch {
            if (isNetworkAvailable) {
                val result =
                    withContext(Dispatchers.IO) { repository.certifyEvent(event = certifiedEvent) }

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
                            repository.saveEventListToDB(eventList = EventManager.calculationEvents)
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
        EventManager.calculationEvents =
            events.filter { it.isDutyStatusChanged() || it.eventType == EventInsertType.CYCLE_RESET.type }
        calculateEvents().also {
            EventManager.eventListGroupByDate = it
            eventListByDate.value = it

            val list = mutableListOf<Event>()
            it.keys.toList().dropLast(1).forEach { date ->
                it[date]?.filterNot { event -> event.certifiedDates.containsDate(date) }
                    ?.let { dayEvents ->
                        list.addAll(dayEvents)
                    }
            }
            eventListRequiresCertification.value = list
        }
        eventList.value = events
    }

    fun getEventsGroupByDate(): Map<String, List<Event>> {
        return EventManager.eventListGroupByDate
    }

    private fun calculateEvents(): Map<String, List<Event>> {
        // set end times of events
        calculateEndTime()

        val calculatedEvents = mutableListOf<Event>()

        EventManager.uiEvents.forEachIndexed { index, event ->
            val startDate =
                LocalDate.parse(event.date, DateTimeFormatter.ofPattern(DATE_FORMAT_yyyy_MM_dd))
            val endDate =
                LocalDate.parse(
                    event.endDate,
                    DateTimeFormatter.ofPattern(DATE_FORMAT_yyyy_MM_dd)
                )

            val numberOfDaysBetweenDates = ChronoUnit.DAYS.between(startDate, endDate).toInt()

            // If start date and end date in the same day
            if (numberOfDaysBetweenDates == 0) {
                // Add current event to list
                calculatedEvents.add(event)

                // If start date and end date are different dates
            } else if (numberOfDaysBetweenDates > 0) {

                // Add event till the end of the graph
                event.copy(
                    endDate = event.date,
                    endTime = context.getString(R.string.twenty_four_hours)
                ).also {
                    calculatedEvents.add(it)
                }

                for (day in 1..numberOfDaysBetweenDates) {
                    val startDateOfNextGraphEvent = startDate.plusDays(day.toLong())
                        .format(DateTimeFormatter.ofPattern(DATE_FORMAT_yyyy_MM_dd))

                    val endDateOfNextGraphEvent = startDate.plusDays(day.toLong())
                        .format(DateTimeFormatter.ofPattern(DATE_FORMAT_yyyy_MM_dd))

                    // By default it's line should be drew till the end of the graph
                    val nextGraphEvent = event.copy(
                        date = startDateOfNextGraphEvent,
                        endDate = endDateOfNextGraphEvent,
                        time = context.getString(R.string.zero_hours),
                        endTime = context.getString(R.string.twenty_four_hours)
                    )

                    // Check if this end day
                    if (day == numberOfDaysBetweenDates) {
                        // If current index is not last index,
                        // then end date/time of current event should be next event's start date/time
                        if (index < EventManager.uiEvents.lastIndex) {
                            nextGraphEvent.endDate =
                                EventManager.uiEvents.elementAt(index + 1).date
                            nextGraphEvent.endTime =
                                EventManager.uiEvents.elementAt(index + 1).time
                        } else { // If current index is last index, then end date/time is current date/time
                            val timezone =
                                Timezone.findByName(sp.getString(USER_TIMEZONE, null) ?: "")

                            val zoneId = ZoneId.of(timezone.value)

                            nextGraphEvent.endDate =
                                LocalDateTime.now(zoneId)
                                    .format(DateTimeFormatter.ofPattern(DATE_FORMAT_yyyy_MM_dd))

                            nextGraphEvent.endTime =
                                LocalDateTime.now(zoneId)
                                    .format(DateTimeFormatter.ofPattern(TIME_FORMAT_HH_mm))
                        }
                    }
                    calculatedEvents.add(nextGraphEvent)
                }
            }
        }
        val map = calculatedEvents.groupBy { it.date ?: "" }.toMutableMap()
        for (i in EventManager.eventListMock) {
            if (!map.containsKey(i)) {
                map[i] = emptyList()
            }
        }
        return map.toSortedMap()
    }

    fun setEventsMock() {
        val currentDay = LocalDate.now()
        EventManager.uiEvents.groupBy { it.date ?: "" }
        EventManager.eventListMock.add(
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
            EventManager.eventListMock.add(date)
        }
    }

    private fun calculateEndTime() {
        EventManager.uiEvents.forEachIndexed { index, event ->
            if (index < EventManager.uiEvents.size - 1) {
                event.endDate = EventManager.uiEvents[index + 1].date
                event.endTime = EventManager.uiEvents[index + 1].time
            } else {
                val timezone =
                    Timezone.findByName(timezone = sp.getString(USER_TIMEZONE, null) ?: "")
                val zoneId = ZoneId.of(timezone.value)
                event.endDate =
                    LocalDateTime.now(zoneId).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                event.endTime =
                    LocalDateTime.now(zoneId).format(DateTimeFormatter.ofPattern("HH:mm"))
            }

            event.calculateDuration()
        }
    }

    fun setEventInsertCode(code: EventCode) {
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


    /**
     * Save last selected duty status in order to open it again when user launches app
     * */
    fun storeCurrentDutyStatus(status: EventCode) {
        sp.getString(CURRENT_DUTY_STATUS, null)?.let { if (it == status.code) return }
        sp.put(CURRENT_DUTY_STATUS, status.code)
    }

    /**
     * Last duty status of user to navigate on first app launch
     */
    fun getCurrentDutyStatus(): EventCode? {
        sp.getString(CURRENT_DUTY_STATUS, null)?.let {
            return EventCode.findByCode(it)
        }
        return null
    }

    /**
     * Remove last duty status if user selected item in bottom navigation is out of duty statuses
     */
    fun removeCurrentDutyStatus() {
        sp.edit {
            remove(CURRENT_DUTY_STATUS)
            commit()
        }
    }

    fun isUserWaitingToSave(): Boolean {
        return if (progress.value == true) {
            hideProgress()
            true
        } else false
    }


    /**
     * Checks for remaining intermediate logs
     */
    fun checkForIntermediateLog(event: Event, activity: Activity) {
        if (event.eventCode.equals(EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING.code)) {
            when {
                isEditedLastEvent -> {
                    sendRemainingLogs(event)

                    // starts sending intermediate logs
                    IntermediateLogHandler.startSendingIntermediateLog(
                        event,
                        activity,
                        remainingMinutes,
                        getUserTimezone().value
                    )

                }
                else -> {

                    IntermediateLogHandler.startSendingIntermediateLog(
                        event,
                        activity,
                        AlarmManager.INTERVAL_HOUR,
                        getUserTimezone().value,
                    )
                }
            }
        } else {
            IntermediateLogHandler.stopSendingIntermediateLog(activity, context)
        }
    }


    /**
     * This method runs(synchronizes remaining intermediate logs with server) when user opens this application first time after reboot
     */
    fun syncRemainingIntermediateLogs(event: Event, activity: Activity) {
        when (event.eventCode) {
            EventCode.INTERMEDIATE_LOG_WITH_CONVENTIONAL_LOCATION_PRECISION.code,
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING.code -> {
                sendRemainingLogs(event)
                startSendingLog(event, remainingMinutes, activity)
            }
            else -> Unit
        }
    }

    /**
     * Returns last event if it's eventCode of Driving or Intermediate log, otherwise null
     */
    fun getLastDrivingLogEvent(events: List<Event>): Event? {
        var event: Event? = null
        try {
            event = events.last {
                it.eventType == EventInsertType.DUTY_STATUS_CHANGE.type
                        || it.eventType == EventInsertType.INTERMEDIATE_LOG.type
                        || it.eventType == EventInsertType.CHANGE_IN_DRIVERS_INDICATION_OF_AUTHORIZED_PERSONNEL_USE_OF_CMV_OR_YARD_MOVES.type
            }
        } catch (e: NoSuchElementException) {
            Log.e("status", "No status has been selected yet")
        }
        return when (event?.eventCode) {
            EventCode.INTERMEDIATE_LOG_WITH_CONVENTIONAL_LOCATION_PRECISION.code,
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING.code -> event
            else -> null
        }
    }


    /**
     * Starts sending logs by setting first trigger time
     */
    private fun startSendingLog(lastEvent: Event, firstTrigger: Long, activity: Activity) {
        IntermediateLogHandler.startSendingIntermediateLog(
            lastEvent,
            activity,
            firstTrigger,
            getUserTimezone().value
        )
    }


    /**
     * Sends all remaining logs to server
     */
    private fun sendRemainingLogs(event: Event) {
        val events = getRemainingIntermediateLogs(event)
        events.forEach(::insertEvent)
    }


    /**
     * Returns List of Intermediate Log events in the period of given
     * event's time to current time. Interval between events is 1 hour
     */
    private fun getRemainingIntermediateLogs(event: Event): List<Event> {
        val intermediateEvents = mutableListOf<Event>()
        var eventDateTime = dateTimeOf(event = event)
        val zoneId = getUserTimezone().value
        val difference = differenceOf(eventDateTime, zoneId = zoneId)
        var startMinutes = 0

        if (event.eventType == EventInsertType.DUTY_STATUS_CHANGE.type) {
            IntermediateLogHandler.fillUpIntermediateEvent(event)
        }

        while (startMinutes + 60 < difference) {
            eventDateTime = eventDateTime.plusHours(1)

            event.apply {
                date = eventDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
                time = eventDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            }

            intermediateEvents.add(event)
            startMinutes += 60
        }

        remainingMinutes = 60 - (difference - startMinutes)
        return intermediateEvents
    }


    /**
     * This method calculates difference of two dates in minutes
     *
     * @param [from] Start [LocalDateTime]
     * @param [to] End [LocalDateTime]. If it's null we assume [to] as a current [LocalDateTime]
     * @param [zoneId] TimeZone id of user
     *
     * @return [Long] Difference in minutes
     */
    fun differenceOf(
        from: LocalDateTime,
        to: LocalDateTime? = null,
        zoneId: String? = null
    ): Long {
        return if (to == null) {
            val currentDateTime = LocalDateTime.now(ZoneId.of(zoneId))
            from.until(currentDateTime, ChronoUnit.MINUTES)
        } else {
            from.until(to, ChronoUnit.MINUTES)
        }
    }


    /**
     * Returns date and time of event as formatted to type of LocalDateTime
     */
    private fun dateTimeOf(event: Event): LocalDateTime {
        return LocalDateTime.parse(
            "${event.date} ${event.time}",
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        )
    }

    /**
     *  Sends driver’s login activity to server
     */
    fun sendLoginEvent() {
        insertEvent(newLoginEvent())
    }

    /**
     *  Sends driver’s logout activity to server
     */
    fun sendLogoutEvent() {
        val event = newLoginEvent()
        event.eventCode = EventCode.AUTHENTICATED_DRIVER_ELD_LOGOUT_ACTIVITY.code
        insertEvent(event)
    }

    /**
     * Returns new instance of Event with login request fields
     */
    private fun newLoginEvent(): Event {
        return Event(
            eventType = EventInsertType.DRIVERS_LOGIN_LOGOUT_ACTIVITY.type,
            eventCode = EventCode.AUTHENTICATED_DRIVER_ELD_LOGIN_ACTIVITY.code,
            date = getFormattedUserDate(),
            time = getFormattedUserTime(),
            totalEngineMiles = 0,
            totalEngineHours = 0,
            dataDiagnosticEventIndicatorStatus = DataDiagnosticEventIndicatorStatusType.NO_ACTIVE_DATA_DIAGNOSTIC_EVENTS_FOR_DRIVER.type,
            malfunctionIndicatorStatus = MalfunctionIndicatorStatusType.NO_ACTIVE_MALFUNCTION.type,
            eventRecordOrigin = EventRecordOriginType.AUTOMATICALLY_RECORDED_BY_ELD.type,
            eventRecordStatus = EventRecordStatusType.ACTIVE.type
        )
    }
}