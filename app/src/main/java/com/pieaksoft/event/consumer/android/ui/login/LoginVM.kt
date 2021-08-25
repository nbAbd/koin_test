package com.pieaksoft.event.consumer.android.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.ui.base.BaseVM
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_CURRENT_USER_ID
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class LoginVM(private val loginRepo: LoginRepo): BaseVM() {
    private val _isSuccessLogin = SingleLiveEvent<Boolean>()
    val isSuccessLogin: LiveData<Boolean> = _isSuccessLogin

    fun login(email: String, password: String) {
        launch {
            when (val response = loginRepo.login(email, password)) {
                is Success -> {
                    Log.e("Login_tag", "good = "+response.data.jwtToken)
                    sp.edit()
                        .putString(
                            SHARED_PREFERENCES_CURRENT_USER_ID,
                            response.data.jwtToken
                        )
                        .apply()
                    _isSuccessLogin.value = true
                }
                is Failure -> {
                    _error.value = response.error
                }
            }
        }

    }
}