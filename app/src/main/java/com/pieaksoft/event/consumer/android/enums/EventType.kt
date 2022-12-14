package com.pieaksoft.event.consumer.android.enums

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EventCode(val code: String) : Parcelable {
    DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY("DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY"),
    DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH("DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH"),
    DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING("DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING"),
    DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING("DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING"),
    INTERMEDIATE_LOG_WITH_CONVENTIONAL_LOCATION_PRECISION("INTERMEDIATE_LOG_WITH_CONVENTIONAL_LOCATION_PRECISION"),
    IMMEDIATE_LOG_WITH_REDUCED_LOCATION_PRECISION("INTERMEDIATE_LOG_WITH_REDUCED_LOCATION_PRECISION"),
    DRIVER_INDICATES_AUTHORIZED_PERSONAL_USE_OF_CMV("DRIVER_INDICATES_AUTHORIZED_PERSONAL_USE_OF_CMV"),
    DRIVER_INDICATES_YARD_MOVES("DRIVER_INDICATES_YARD_MOVES"),
    DRIVER_INDICATION_FOR_PC_YM_AND_WT_CLEARED("DRIVER_INDICATION_FOR_PC_YM_AND_WT_CLEARED"),
    DRIVER_FIRST_CERTIFICATION_OF_DAILY_RECORD("DRIVER_FIRST_CERTIFICATION_OF_DAILY_RECORD"),
    AUTHENTICATED_DRIVER_ELD_LOGIN_ACTIVITY("AUTHENTICATED_DRIVER_ELD_LOGIN_ACTIVITY"),
    AUTHENTICATED_DRIVER_ELD_LOGOUT_ACTIVITY("AUTHENTICATED_DRIVER_ELD_LOGOUT_ACTIVITY"),
    ENGINE_POWER_UP_WITH_CONVENTIONAL_LOCATION_PRECISION("ENGINE_POWER_UP_WITH_CONVENTIONAL_LOCATION_PRECISION"),
    ENGINE_POWER_UP_WITH_REDUCED_LOCATION_PRECISION("ENGINE_POWER_UP_WITH_REDUCED_LOCATION_PRECISION"),
    ENGINE_SHUT_DOWN_WITH_CONVENTIONAL_LOCATION_PRECISION("ENGINE_SHUT_DOWN_WITH_CONVENTIONAL_LOCATION_PRECISION"),
    ENGINE_SHUT_DOWN_WITH_REDUCED_LOCATION_PRECISION("ENGINE_SHUT_DOWN_WITH_REDUCED_LOCATION_PRECISION"),
    ELD_MALFUNCTION_LOGGED("ELD_MALFUNCTION_LOGGED"),
    ELD_MALFUNCTION_CLEARED("ELD_MALFUNCTION_CLEARED"),
    DATA_DIAGNOSTIC_EVENT_LOGGED("DATA_DIAGNOSTIC_EVENT_LOGGED"),
    DATA_DIAGNOSTIC_EVENT_CLEARED("DATA_DIAGNOSTIC_EVENT_CLEARED"),
    CYCLE_RESET("CYCLE_RESET"),
    NONE("NONE");

    companion object {
        private val DEFAULT = NONE
        fun findByCode(code: String) = values().find { it.code.contentEquals(code) } ?: DEFAULT
    }
}

fun String.toReadable(): String {
    return when (this) {
        EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_OFF_DUTY.code -> "OFF DUTY"
        EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_SLEEPER_BERTH.code -> "SLEEPER BERTH"
        EventCode.DRIVER_DUTY_STATUS_CHANGED_TO_DRIVING.code -> "DRIVING"
        EventCode.DRIVER_DUTY_STATUS_ON_DUTY_NOT_DRIVING.code -> "ON DUTY"
        EventCode.DRIVER_INDICATES_AUTHORIZED_PERSONAL_USE_OF_CMV.code -> "PERSONAL USE OF CMV"
        EventCode.DRIVER_INDICATES_YARD_MOVES.code -> "YARD MOVES"
        else -> ""
    }
}