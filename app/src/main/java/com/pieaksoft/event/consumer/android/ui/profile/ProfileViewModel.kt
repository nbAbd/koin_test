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
import kotlinx.coroutines.launch

class ProfileViewModel(val app: Application, private val profileRepository: ProfileRepository) :
    BaseViewModel(app) {

    val primaryDriver: LiveData<List<Profile>> = profileRepository.getPrimaryProfiles().asLiveData()

    val additionalDriver: LiveData<List<Profile>> =
        profileRepository.getAdditionalProfiles().asLiveData()

    val needUpdateObservable = SingleLiveEvent<Boolean>()

    val doesNoticeExistingProfile: MutableLiveData<Boolean> = MutableLiveData()

    fun isAuth(): Boolean {
        return sp.getString(SHARED_PREFERENCES_CURRENT_USER_ID, "") != ""
    }


    fun getProfile(isAdditional: Boolean = false) {
        launch {
            val token = if (isAdditional) sp.getString(
                SHARED_PREFERENCES_ADDITIONAL_USER_ID,
                ""
            ) else sp.getString(
                SHARED_PREFERENCES_MAIN_USER_ID, sp.getString(
                    SHARED_PREFERENCES_CURRENT_USER_ID, ""
                )
            )

            when (val response = profileRepository.getProfile(token ?: "")) {
                is Success -> {
                    response.data.let {
                        if (isAdditional) {
                            // Return if additional profile is the same with primary
                            if (primaryDriver.value.takeIf { list -> list?.isNotEmpty() == true }
                                    ?.last()?.id == it.id) {
                                doesNoticeExistingProfile.value = true
                                return@let
                            }

                            // if there any additional profile delete them
                            if (profileRepository.getAdditionalProfiles()
                                    .asLiveData().value?.isNotEmpty() == true
                            ) {
                                profileRepository.deleteAdditionalProfiles()
                            }

                            // change profile status to additional profile
                            val additionalProfile = it.copy(isAdditional = true)
                            profileRepository.saveProfile(profile = additionalProfile)
                        } else {

                            // if primary profile exists, then update it
                            if (profileRepository.isProfileExists(id = it.id)) {
                                profileRepository.update(profile = it)
                            } else {
                                if (profileRepository.getPrimaryProfiles()
                                        .asLiveData().value?.isNotEmpty() == true
                                ) {
                                    profileRepository.deletePrimaryProfiles()
                                }

                                // save profile
                                profileRepository.saveProfile(it)

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
        }
    }

    fun swapDrivers() {
        val mainToken = sp.getString(SHARED_PREFERENCES_MAIN_USER_ID, "")
        val additionalToken = sp.getString(SHARED_PREFERENCES_ADDITIONAL_USER_ID, "")

        sp.edit().putString(SHARED_PREFERENCES_ADDITIONAL_USER_ID, mainToken).apply()
        sp.edit().putString(SHARED_PREFERENCES_CURRENT_USER_ID, additionalToken).apply()
        sp.edit().putString(SHARED_PREFERENCES_MAIN_USER_ID, additionalToken).apply()
        needUpdateObservable.postValue(true)
    }

    private fun saveUserTimezone(timezone: String?) {
        sp.put(USER_TIMEZONE, timezone)
    }

    fun getUserTimezone(): String? {
        return sp.getString(USER_TIMEZONE, null)
    }
}