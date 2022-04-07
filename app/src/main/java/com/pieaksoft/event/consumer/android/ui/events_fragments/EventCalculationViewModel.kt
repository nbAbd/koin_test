package com.pieaksoft.event.consumer.android.ui.events_fragments

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.enums.EventInsertType
import com.pieaksoft.event.consumer.android.enums.Timezone
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.utils.Storage
import com.pieaksoft.event.consumer.android.utils.USER_TIMEZONE
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit


class EventCalculationViewModel(val app: Application) : BaseViewModel(app) {
    companion object {
        private val MINUTE = TimeUnit.MINUTES.toMillis(1)

        private val OFF_DUTY_CODES = arrayOf(
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY,
            EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH,
            EventCode.DRIVER_INDICATES_AUTHORIZED_PERSONAL_USE_OF_CMV
        )
    }

    /**
     * @property maxOnDutyMillis is 70 hours in millis
     *
     * Max on duty in millis per day
     * @property onDutyMillis is 14 hours in millis
     *
     * Driving limit per day. And it's in 14h on duty
     * @property onDutyDrivingLimitMillis 11 hours in millis
     *
     * Millis where driver can drive nonstop
     * @property onDutyBreakInMillis is 8 hours in millis
     *
     * When driver's status/statuses are consecutively in off duty and it's greater than 10 h
     * we should reset 14h
     * @property needResetOnDutyMillis is 10 hours in millis.
     *
     * When driver's status/statuses are consecutively in off duty and it's greater than 34 h
     * we should reset 70h
     * @property needResetMaxOnDutyMillis is 34 hours in millis
     *
     * @property thirtyMinutesInMillis is 30 min to check whether driver is driving without rest
     *
     * @property isNeedBreak is boolean to sign break status
     */

    var maxOnDutyMillis: Long = 70L.toMillis()

    var onDutyMillis: Long = 14L.toMillis()

    var onDutyDrivingLimitMillis: Long = 11L.toMillis()

    var onDutyBreakInMillis: Long = 8L.toMillis()

    var needResetOnDutyMillis: Long = 10L.toMillis()

    var needResetMaxOnDutyMillis: Long = 34L.toMillis()

    var needResetMaxOnDutyInEightDays: Long = 8

    var thirtyMinutesInMillis: Long = 30 * 60 * 1000

    var isNeedBreak: Boolean = false


    private val _maxOnDuty: MutableLiveData<Long> = MutableLiveData()
    val maxOnDuty: LiveData<Long> = _maxOnDuty

    private val _onDuty: MutableLiveData<Long> = MutableLiveData()
    val onDuty: LiveData<Long> = _onDuty

    private val _onDutyDrivingLimit: MutableLiveData<Long> = MutableLiveData()
    val onDutyDrivingLimit: LiveData<Long> = _onDutyDrivingLimit

    private val _onDutyBreakIn: MutableLiveData<Long> = MutableLiveData()
    val onDutyBreakIn: LiveData<Long> = _onDutyBreakIn


    // This the warning boolean, which indicates that on duty cycle is more than 70h
    private val _maxOnDutyExceedingTheLimitWarning = MutableLiveData<Boolean>()
    val maxOnDutyExceedingTheLimitWarning = _maxOnDutyExceedingTheLimitWarning

    private val _onDutyExceedingTheLimitWarning = MutableLiveData<Boolean>()
    val onDutyExceedingTheLimitWarning = _onDutyExceedingTheLimitWarning


    fun calculate(then: () -> Unit) {
        val eventList = Storage.eventList

        eventList.forEachIndexed { index, currentEvent ->
            currentEvent.prepareEndDateTime()
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
                            sendCycleResetRequest()
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

    private fun sendCycleResetRequest() {
        Event().also {
            it.eventType = EventInsertType.CYCLE_RESET.type
            it.eventCode = EventCode.CYCLE_RESET.code
            // TODO send request here
        }
        resetMillis()
    }

    private fun Long.toMillis() = this * 60 * 60 * 1000

    private fun Event.durationInMillis(): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTimeString = "$date $time"
        val startDateTime = LocalDateTime.parse(dateTimeString, formatter)
        val endDateTimeString = "$endDate $endTime"
        val endDateTime = LocalDateTime.parse(endDateTimeString, formatter)
        return startDateTime.until(endDateTime, ChronoUnit.MILLIS)
    }

    private fun Event.prepareEndDateTime() {
        if (this.endDate == null) {
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