package com.pieaksoft.event.consumer.android.ui.activities.login

import android.app.Application
import androidx.lifecycle.LiveData
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_ADDITIONAL_USER_ID
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_CURRENT_USER_ID
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class LoginViewModel(
    val app: Application,
    private val loginRepo: LoginRepo,
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
}