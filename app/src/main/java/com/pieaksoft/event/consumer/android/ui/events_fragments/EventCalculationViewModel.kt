package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pieaksoft.event.consumer.android.enums.*
import com.pieaksoft.event.consumer.android.events.EventViewModel
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.utils.*
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit


class EventCalculationViewModel(val app: Application, private val eventViewModel: EventViewModel) :
    BaseViewModel(app) {
    companion object {
        // Minute in millis
        private val MINUTE = TimeUnit.MINUTES.toMillis(1)

        // Off duty statuses
        private val OFF_DUTY_CODES = arrayOf(
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY,
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH,
            EventCode.DRIVER_INDICATES_AUTHORIZED_PERSONAL_USE_OF_CMV
        )
    }


    /**
     * Max on duty in millis per week
     *
     * [maxOnDutyMillis] - is 70 hours in millis
     */
    private var maxOnDutyMillis: Long = 70L.toMillis()


    /**
     * Max on duty in millis per day
     *
     * [onDutyMillis] - is 14 hours in millis
     */
    private var onDutyMillis: Long = 14L.toMillis()


    /**
     * Driving limit per day. And it's in 14h on duty
     *
     * [onDutyDrivingLimitMillis] - 11 hours in millis
     */
    private var onDutyDrivingLimitMillis: Long = 11L.toMillis()


    /**
     * Millis where driver can drive nonstop
     *
     * [onDutyBreakInMillis] - is 8 hours in millis
     */
    private var onDutyBreakInMillis: Long = 8L.toMillis()


    /**
     * When driver's status/statuses are consecutively in off duty and it's greater than 10 h
     * we should reset 14h
     *
     * [needResetOnDutyMillis] - is 10 hours in millis.
     */
    private var needResetOnDutyMillis: Long = 10L.toMillis()


    /**
     * When driver's status/statuses are consecutively in off duty and it's greater than 34 h
     * we should reset 70h
     *
     * [needResetMaxOnDutyMillis] - is 34 hours in millis
     */
    var needResetMaxOnDutyMillis: Long = 34L.toMillis()


    /**
     * Number of days to reset cycle
     *
     * [needResetCycleDays] - 8 days
     */
    private var needResetCycleDays: Long = 8

    /**
     * [thirtyMinutesInMillis] - 30 minutes in millis
     *
     * Driver should take at least
     * 30 min break after nonstop 8h driving
     */
    private var thirtyMinutesInMillis: Long = 30 * 60 * 1000


    /**
     * Indicator whether driver should take break
     */
    var isNeedBreak: Boolean = false


    /**
     * 70 hours
     */
    private val _maxOnDuty: MutableLiveData<Long> = MutableLiveData()
    val maxOnDuty: LiveData<Long> = _maxOnDuty

    /**
     * 14 hours
     */
    private val _onDuty: MutableLiveData<Long> = MutableLiveData()
    val onDuty: LiveData<Long> = _onDuty

    /**
     * 11 hours
     */
    private val _onDutyDrivingLimit: MutableLiveData<Long> = MutableLiveData()
    val onDutyDrivingLimit: LiveData<Long> = _onDutyDrivingLimit


    /**
     * 8 hours
     */
    private val _onDutyBreakIn: MutableLiveData<Long> = MutableLiveData()
    val onDutyBreakIn: LiveData<Long> = _onDutyBreakIn


    /**
     * Warning boolean, which indicates that on duty cycle is more than 70h
     */
    private val _maxOnDutyExceedingTheLimitWarning = MutableLiveData<Boolean>()
    val maxOnDutyExceedingTheLimitWarning = _maxOnDutyExceedingTheLimitWarning


    /**
     * Warning boolean, which indicates that on duty cycle is more than 14h
     */
    private val _onDutyExceedingTheLimitWarning = MutableLiveData<Boolean>()
    val onDutyExceedingTheLimitWarning = _onDutyExceedingTheLimitWarning


    fun calculate(then: () -> Unit) {
        val eventList = EventManager.eventList

        eventList.forEachIndexed { index, currentEvent ->
            currentEvent.setEndDateTime()
            val duration = currentEvent.durationInMillis()

            currentEvent.eventCode?.let { eventCode ->
                when (EventCode.findByCode(code = eventCode)) {
                    in OFF_DUTY_CODES -> {
                        // Minus current event duration from MAX_OFF_DUTY_HOURS
                        needResetMaxOnDutyMillis -= duration

                        // Then check if it's less than or equal to 0, if it's true reset
                        if (needResetMaxOnDutyMillis <= 0) {
                            // 34h
                            resetMaxOffDutyHours()
                            // 70h 14h 11h 8h
                            sendResetCycleRequest()
                            // break loop
                            return@forEachIndexed
                        }


                        // Minus current event duration from OFF_DUTY_10H_MILLIS
                        needResetOnDutyMillis -= duration

                        // Then check if it's less than or equal to 0, if it's true reset 14h 11h 8h
                        if (needResetOnDutyMillis <= 0) {
                            resetOnDutyCycleHours()
                        }


                        // Minus current event duration from 30 MIN
                        thirtyMinutesInMillis -= duration

                        // Then check if it's less than or equal to 0, if it's true reset 8h
                        if (thirtyMinutesInMillis <= 0) {
                            isNeedBreak = false
                            resetNonstopCycleHours()
                        }


                        // If MAX_OFF_DUTY_HOURS not less or equal to 0 continue
                        // Get next event of current event, then check
                        val nextEvent = eventList.elementAtOrNull(index.inc())

                        // If next event exists,then:
                        nextEvent?.let {
                            // Check if next event is not OFF_DUTY
                            if (it.isNotOffDuty) {
                                // Then reset 34h 10h
                                resetMaxOffDutyHours()
                            }
                        }
                    }

                    EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING -> {
                        onDutyBreakInMillis -= duration

                        maxOnDutyMillis -= duration
                        onDutyMillis -= duration
                        onDutyDrivingLimitMillis -= duration


                        isNeedBreak = duration >= (30 * 60 * 1000)
                    }

                    EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING,
                    EventCode.DRIVER_INDICATES_YARD_MOVES -> {
                        maxOnDutyMillis -= duration
                        onDutyMillis -= duration

                        // Minus current event duration from 30 MIN
                        thirtyMinutesInMillis -= duration

                        // Then check if it's less than or equal to 0, if it's true reset 8h
                        if (thirtyMinutesInMillis <= 0) {
                            isNeedBreak = false
                            resetNonstopCycleHours()
                        }
                    }

                    EventCode.CYCLE_RESET -> {
                        resetMillis()
                    }
                    else -> Unit
                }
            }
        }

        if (maxOnDutyMillis <= 0) {
            _maxOnDutyExceedingTheLimitWarning.value = true
        }

        if (onDutyMillis <= 0) {
            _onDutyExceedingTheLimitWarning.value = true
        }

        _maxOnDuty.postValue(maxOnDutyMillis)
        _onDuty.postValue(onDutyMillis)
        _onDutyDrivingLimit.postValue(onDutyDrivingLimitMillis)
        _onDutyBreakIn.postValue(onDutyBreakInMillis)

        then()
    }


    fun checkResetCycleDate() {
        sp.getResetCycleStartDate()?.let { startDateString ->
            val zoneId = ZoneId.of(getUserTimezone().value)
            val numberOfDays = daysBetweenDates(start = startDateString, zoneId = zoneId).toLong()

            if (numberOfDays == needResetCycleDays) {
                sendResetCycleRequest()
            }
        }
    }

    private val Event.isNotOffDuty: Boolean
        get() = EventCode.findByCode(code = eventCode ?: "") !in OFF_DUTY_CODES


    private fun resetNonstopCycleHours() {
        thirtyMinutesInMillis = 30 * 60 * 1000
        onDutyBreakInMillis = 8L.toMillis()
    }


    private fun resetDrivingLimitCycleHours() {
        onDutyDrivingLimitMillis = 11L.toMillis()
    }


    private fun resetOnDutyCycleHours() {
        // Reset 10 h too
        needResetOnDutyMillis = 10L.toMillis()

        // 14 h
        onDutyMillis = 14L.toMillis()

        // 11 h
        resetDrivingLimitCycleHours()

        // 8 h
        resetNonstopCycleHours()
    }


    private fun resetTotalOnDutyCycleHours() {
        maxOnDutyMillis = 70L.toMillis()
    }


    private fun resetMaxOffDutyHours() {
        needResetMaxOnDutyMillis = 34L.toMillis()
        needResetOnDutyMillis = 10L.toMillis()
    }


    fun resetMillis() {
        resetTotalOnDutyCycleHours()
        resetOnDutyCycleHours()
    }

    private fun sendResetCycleRequest() {
        val event = Event(
            eventType = EventInsertType.CYCLE_RESET.type,
            eventCode = EventCode.CYCLE_RESET.code,
            date = getFormattedUserDate(true),
            time = getFormattedUserTime(true),
            totalEngineMiles = null,
            eventRecordOrigin = EventRecordOriginType.AUTOMATICALLY_RECORDED_BY_ELD.type,
            eventRecordStatus = EventRecordStatusType.ACTIVE.type,
            malfunctionIndicatorStatus = MalfunctionIndicatorStatusType.NO_ACTIVE_MALFUNCTION.type,
            dataDiagnosticEventIndicatorStatus = DataDiagnosticEventIndicatorStatusType.NO_ACTIVE_DATA_DIAGNOSTIC_EVENTS_FOR_DRIVER.type,
            driverLocationDescription = null,
            dutyStatus = null,
            certification = null,
            certifiedDates = null,
            recordOrigin = null,
            createdAt = null,
            distanceSinceLastValidCoordinates = null,
            eventSequenceId = null,
            endDate = null,
            endTime = null,
            toAddress = null,
            fromAddress = null,
            trailer = null,
            comment = null,
            isSyncWithServer = true,
        )

        eventViewModel.insertEvent(e = event)
    }

    /**
     * Converts given minutes to millis
     * @return [Long] - Minutes in millis
     */
    private fun Long.toMillis() = this * 60 * 60 * 1000


    /**
     * Calculates duration of event in millis
     * @return [Long] - duration in millis
     */
    private fun Event.durationInMillis(): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTimeString = "$date $time"
        val startDateTime = LocalDateTime.parse(dateTimeString, formatter)
        val endDateTimeString = "$endDate $endTime"
        val endDateTime = LocalDateTime.parse(endDateTimeString, formatter)
        return startDateTime.until(endDateTime, ChronoUnit.MILLIS)
    }


    /**
     * Method checks if [Event] object has end date and time.
     * If it is missing current date/time i should be set
     */
    private fun Event.setEndDateTime() {
        if (endDate == null || endDate.contentEquals("")) {
            val timezone = Timezone.findByName(timezone = sp.getString(USER_TIMEZONE, null) ?: "")
            val zoneId = ZoneId.of(timezone.value)

            endDate = LocalDateTime.now(zoneId).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            endTime = LocalDateTime.now(zoneId).format(DateTimeFormatter.ofPattern("HH:mm"))
        }
    }


    private var totalOnDutyCounterJob: Job? = null

    fun startTotalOnDutyCounter() {
        totalOnDutyCounterJob = CoroutineScope(Dispatchers.IO).async {
            while (isActive) {
                delay(MINUTE)
                countdownTotals()
            }
        }
    }

    fun cancelTotalOnDutyCounter() = totalOnDutyCounterJob?.cancel()

    private fun countdownTotals() {
        maxOnDutyMillis -= MINUTE
        _maxOnDuty.postValue(maxOnDutyMillis)
    }


    private var onDutyCounterJob: Job? = null

    fun startOnDutyCounter() {
        onDutyCounterJob = CoroutineScope(Dispatchers.IO).async {
            while (isActive) {
                delay(MINUTE)
                countdownOnDuty()
            }
        }
    }

    fun cancelOnDutyCounter() = onDutyCounterJob?.cancel()

    private fun countdownOnDuty() {
        onDutyMillis -= MINUTE
        _onDuty.postValue(onDutyMillis)
    }


    private var drivingLimitCounterJob: Job? = null

    fun startDrivingLimitCounter() {
        drivingLimitCounterJob = CoroutineScope(Dispatchers.IO).async {
            while (isActive) {
                delay(MINUTE)
                countdownDrivingLimit()
            }
        }
    }

    fun cancelDrivingLimitCounter() = drivingLimitCounterJob?.cancel()

    private fun countdownDrivingLimit() {
        onDutyDrivingLimitMillis -= MINUTE
        _onDutyDrivingLimit.postValue(onDutyDrivingLimitMillis)
    }


    private var breakInCounterJob: Job? = null

    fun startBreakInCounter() {
        breakInCounterJob = CoroutineScope(Dispatchers.IO).async {
            while (isActive) {
                delay(MINUTE)
                countdownBreakInMinutes()
            }
        }
    }

    fun cancelBreakInCounter() = breakInCounterJob?.cancel()

    private fun countdownBreakInMinutes() {
        onDutyBreakInMillis -= MINUTE
        _onDutyBreakIn.postValue(onDutyBreakInMillis)
    }
}