package com.pieaksoft.event.consumer.android.ui.profile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.model.profile.Profile
import com.pieaksoft.event.consumer.android.ui.base.BaseViewModel
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_ADDITIONAL_USER_ID
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_CURRENT_USER_ID
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import com.pieaksoft.event.consumer.android.utils.put
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(val app: Application, private val profileRepository: ProfileRepository) :
    BaseViewModel(app) {

    val primaryDriver: MutableLiveData<Profile?> = MutableLiveData<Profile?>()

    val additionalDriver: MutableLiveData<Profile?> = MutableLiveData<Profile?>()

    val needUpdateObservable = SingleLiveEvent<Boolean>()

    fun isAuth(): Boolean {
        return sp.getString(SHARED_PREFERENCES_CURRENT_USER_ID, "") != ""
    }

    private fun getProfile() {
        launch(Dispatchers.IO) {
            val token = sp.getString(
                SHARED_PREFERENCES_CURRENT_USER_ID, ""
            )

            if (isNetworkAvailable) {
                when (val response =
                    withContext(Dispatchers.IO) { profileRepository.getProfile(token ?: "") }) {
                    is Success -> {
                        response.data.let {
                            // if primary profile exists, then update it
                            if (profileRepository.isProfileExists(id = it.id)) {
                                profileRepository.update(profile = it)
                            } else {
                                profileRepository.getPrimaryProfile()?.let {
                                    profileRepository.deletePrimaryProfiles()
                                }

                                token?.let { token -> it.token = token }

                                it.isAdditional = false

                                // save profile
                                profileRepository.saveProfile(it)

                                primaryDriver.postValue(it)

                                // save timezone
                                saveUserTimezone(timezone = it.user.homeTerminalTimezone)
                            }
                        }
                    }
                    is Failure -> {
                        _error.value = response.error
                    }
                }
            } else {
                primaryDriver.postValue(profileRepository.getPrimaryProfile())
            }
        }
    }

    private fun getCoDriverProfile() {
        launch(Dispatchers.IO) {
            val token = sp.getString(
                SHARED_PREFERENCES_ADDITIONAL_USER_ID, ""
            )

            if (isNetworkAvailable) {
                when (val response =
                    withContext(Dispatchers.IO) { profileRepository.getProfile(token ?: "") }) {
                    is Success -> {
                        response.data.let {

                            // if there any additional profile delete them
                            profileRepository.getAdditionalProfile()?.let {
                                profileRepository.deleteAdditionalProfiles()
                            }

                            // change profile status to additional profile
                            val additionalProfile = it.copy(isAdditional = true)

                            profileRepository.getPrimaryProfile()?.let { primaryProfile ->
                                additionalProfile.user.coDriverId = primaryProfile.id.toInt()
                            }
                            token.let { token -> additionalProfile.token = token }

                            profileRepository.saveProfile(profile = additionalProfile)

                            additionalDriver.postValue(additionalProfile)
                        }
                    }
                    is Failure -> _error.value = response.error
                }
            } else {
                additionalDriver.postValue(profileRepository.getAdditionalProfile())
            }
        }
    }

    suspend fun swapDrivers() {
        withContext(Dispatchers.IO) {
            val currentToken = sp.getString(SHARED_PREFERENCES_CURRENT_USER_ID, "")
            val additionalToken = sp.getString(SHARED_PREFERENCES_ADDITIONAL_USER_ID, "")

            sp.put(SHARED_PREFERENCES_ADDITIONAL_USER_ID, currentToken)
            sp.put(SHARED_PREFERENCES_CURRENT_USER_ID, additionalToken)

            profileRepository.getAdditionalProfile()?.let {
                it.isAdditional = false
                profileRepository.saveProfile(it)
            }

            profileRepository.getPrimaryProfile()?.let {
                it.isAdditional = true
                profileRepository.saveProfile(it)
            }
            needUpdateObservable.postValue(true)
        }
    }

    fun getDriversInfo() {
        getProfile()
        if (isCooDriverExist()) {
            getCoDriverProfile()
        }
    }

    private fun isCooDriverExist(): Boolean {
        return sp.getString(SHARED_PREFERENCES_ADDITIONAL_USER_ID, "") != ""
    }

    suspend fun getCoDriverId(): Int? {
        return withContext(Dispatchers.IO) { profileRepository.getAdditionalProfile()?.user?.coDriverId }
    }

    suspend fun isPersonalUseAllowed(): Boolean? {
        return withContext(Dispatchers.IO) { profileRepository.getPrimaryProfile()?.user?.allowedPc }
    }

    suspend fun isYardMoveAllowed(): Boolean? {
        return withContext(Dispatchers.IO) { profileRepository.getPrimaryProfile()?.user?.allowedYm }
    }

    suspend fun deleteCoDriver() {
        withContext(Dispatchers.IO) {
            profileRepository.deleteAdditionalProfiles()
            sp.put(SHARED_PREFERENCES_ADDITIONAL_USER_ID, "")
        }
    }
}