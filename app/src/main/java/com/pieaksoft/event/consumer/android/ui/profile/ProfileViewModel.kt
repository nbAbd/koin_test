package com.pieaksoft.event.consumer.android.ui.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.model.profile.Profile
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(val app: Application, private val profileRepository: ProfileRepository) :
    BaseViewModel(app) {

    val primaryDriver: MutableLiveData<Profile?> = MutableLiveData<Profile?>()

    val additionalDriver: MutableLiveData<Profile?> = MutableLiveData<Profile?>()

    val needUpdateObservable = SingleLiveEvent<Boolean>()

    fun isAuth(): Boolean {
        return sp.getString(SHARED_PREFERENCES_CURRENT_USER_ID, "") != ""
    }

    fun getProfile(isAdditional: Boolean = false, fromLocal: Boolean = false) {
        launch(Dispatchers.IO){
            val token = if (isAdditional) sp.getString(
                SHARED_PREFERENCES_ADDITIONAL_USER_ID,
                ""
            ) else sp.getString(
                SHARED_PREFERENCES_CURRENT_USER_ID, ""
            )
            if (isNetworkAvailable && !fromLocal) {
                when (val response = profileRepository.getProfile(token ?: "")) {
                    is Success -> {
                        response.data.let {
                            if (isAdditional) {

                                // if there any additional profile delete them
                                if (profileRepository.getAdditionalProfile() != null
                                ) {
                                    profileRepository.deleteAdditionalProfiles()
                                }

                                // change profile status to additional profile
                                val additionalProfile = it.copy(isAdditional = true)
                                token?.let { token -> additionalProfile.token = token }

                                additionalDriver.postValue(additionalProfile)
                                profileRepository.saveProfile(profile = additionalProfile)
                            } else {

                                // if primary profile exists, then update it
                                if (profileRepository.isProfileExists(id = it.id)) {
                                    profileRepository.update(profile = it)
                                } else {
                                    if (profileRepository.getPrimaryProfile() != null
                                    ) {
                                        profileRepository.deletePrimaryProfiles()
                                    }

                                    token?.let { token -> it.token = token }

                                    // save profile
                                    profileRepository.saveProfile(it)

                                    primaryDriver.postValue(it)

                                    // save timezone
                                    saveUserTimezone(timezone = it.user.homeTerminalTimezone)
                                }
                            }
                        }
                    }
                    is Failure -> {
                        _error.value = response.error
                    }
                }
            } else {
                if (isAdditional) {
                    additionalDriver.postValue(profileRepository.getAdditionalProfile())
                } else primaryDriver.postValue(profileRepository.getPrimaryProfile())
            }
        }
    }

    fun swapDrivers() {
        launch(Dispatchers.IO) {
            val currentToken = sp.getString(SHARED_PREFERENCES_CURRENT_USER_ID, "")
            val additionalToken = sp.getString(SHARED_PREFERENCES_ADDITIONAL_USER_ID, "")

            sp.edit().putString(SHARED_PREFERENCES_ADDITIONAL_USER_ID, currentToken).apply()
            sp.edit().putString(SHARED_PREFERENCES_CURRENT_USER_ID, additionalToken).apply()

            profileRepository.getAdditionalProfile()?.let {
                it.isAdditional = false
                profileRepository.saveProfile(it)
            }

            profileRepository.getPrimaryProfile()?.let {
                it.isAdditional = true
                profileRepository.saveProfile(it)
            }

            getDriversInfo()
        }
    }

    fun getDriversInfo() {
        getProfile()
        if (isCooDriverExist()) {
            getProfile(true)
        }
    }

    private fun isCooDriverExist(): Boolean {
        return sp.getString(SHARED_PREFERENCES_ADDITIONAL_USER_ID, "") != ""
    }
}