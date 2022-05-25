package com.pieaksoft.event.consumer.android.ui.activities.login

import android.app.Application
import androidx.lifecycle.LiveData
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.enums.*
import com.pieaksoft.event.consumer.android.events.EventsRepository
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_ADDITIONAL_USER_ID
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_CURRENT_USER_ID
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    val app: Application,
    private val loginRepo: LoginRepo,
    private val eventsRepository: EventsRepository,
    private val profileViewModel: ProfileViewModel
) : BaseViewModel(app) {
    companion object {
        const val DRIVER = "DRIVER"
    }

    private val _isSuccessLogin = SingleLiveEvent<Pair<Boolean, Int?>>()
    val isSuccessLogin: LiveData<Pair<Boolean, Int?>> = _isSuccessLogin

    fun login(email: String, password: String, isAdditionalDriver: Boolean = false) {
        showProgress()
        launch {
            when (val response = loginRepo.login(email.trim(), password.trim())) {
                is Success -> {
                    val isDriver = response.data.authorities?.contains(DRIVER) ?: false
                    if (isDriver) {
                        if (isAdditionalDriver) {
                            sp.edit()
                                .putString(
                                    SHARED_PREFERENCES_ADDITIONAL_USER_ID,
                                    response.data.jwtToken
                                )
                                .apply()
                            profileViewModel.getProfile(true)
                        } else {
                            sp.edit()
                                .putString(
                                    SHARED_PREFERENCES_CURRENT_USER_ID,
                                    response.data.jwtToken
                                )
                                .apply()
                            profileViewModel.getProfile()
                            sendLoginEvent()
                        }
                        _isSuccessLogin.value = Pair(true, null)

                    } else {
                        _isSuccessLogin.value = Pair(false, R.string.only_drivers_can_login)
                    }
                    hideProgress()
                }
                is Failure -> {
                    hideProgress()
                    _error.value = response.error
                }
            }
        }
    }

    /**
     *  Sends driver’s login activity to server
     */
    fun sendLoginEvent() {
        launch(Dispatchers.IO) {
            eventsRepository.insertEvent(newLoginEvent())
        }
    }

    /**
     *  Sends driver’s logout activity to server
     */
    fun sendLogoutEvent(then: () -> Unit) {
        launch {
            val event = newLoginEvent().copy(
                eventCode = EventCode.AUTHENTICATED_DRIVER_ELD_LOGOUT_ACTIVITY.code
            )
            withContext(Dispatchers.IO) {
                eventsRepository.insertEvent(event)
                then()
            }
        }
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