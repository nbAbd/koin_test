package com.pieaksoft.event.consumer.android.ui.activities.login

import android.app.Application
import androidx.lifecycle.LiveData
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_ADDITIONAL_USER_ID
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_CURRENT_USER_ID
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_MAIN_USER_ID
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class LoginViewModel(val app: Application, private val loginRepo: LoginRepo): BaseViewModel(app) {
    private val _isSuccessLogin = SingleLiveEvent<Boolean>()
    val isSuccessLogin: LiveData<Boolean> = _isSuccessLogin

    fun login(email: String, password: String, isAdditionalDriver: Boolean = false) {
        showProgress()
        launch {
            when (val response = loginRepo.login(email.trim(), password.trim())) {
                is Success -> {
                    if (isAdditionalDriver) {
                        sp.edit()
                            .putString(
                                SHARED_PREFERENCES_ADDITIONAL_USER_ID,
                                response.data.jwtToken
                            )
                            .apply()
                    } else {
                    sp.edit()
                        .putString(
                            SHARED_PREFERENCES_CURRENT_USER_ID,
                            response.data.jwtToken
                        )
                        .apply()
                    sp.edit()
                        .putString(
                            SHARED_PREFERENCES_MAIN_USER_ID,
                            response.data.jwtToken
                        )
                        .apply()
                }
                    _isSuccessLogin.value = true
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