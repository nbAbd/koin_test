package com.pieaksoft.event.consumer.android.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pieaksoft.event.consumer.android.model.Failure
import com.pieaksoft.event.consumer.android.model.ProfileModel
import com.pieaksoft.event.consumer.android.model.Success
import com.pieaksoft.event.consumer.android.ui.base.BaseVM
import com.pieaksoft.event.consumer.android.utils.SHARED_PREFERENCES_CURRENT_USER_ID
import com.pieaksoft.event.consumer.android.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File
import java.net.HttpURLConnection

class ProfileVM(private val repo: ProfileRepo) : BaseVM() {

    private val _result = SingleLiveEvent<ProfileModel>()
    val result: LiveData<ProfileModel> = _result

    fun isAuth(): Boolean {
        return sp.getString(SHARED_PREFERENCES_CURRENT_USER_ID, "") != ""
    }


    fun getProfile() {
        launch {
            when (val response = repo.getProfile()) {
                is Success -> {
                    response.data.let {
                        _result.value = it
                    }
                }
                is Failure -> {
                    _error.value = response.error
                }
            }
        }
    }
//
//    fun updateProfile(name: String?, phone: String?, file: File) {
//        _progress.postValue(true)
//        launch {
//            when (val response = homeRepository.updateProfile(name, phone, file)) {
//                is Success -> {
//                    _progress.postValue(false)
//                    _updateResult.value = response.data
//                    Log.e("ERROR", "${response.data.displayName}")
//                }
//                is Failure -> {
//                    _progress.postValue(false)
//                    _error.postValue(response.error)
//                }
//            }
//        }
//    }
//
//    fun updateProfile(name: String?, phone: String?) {
//        _progress.postValue(true)
//        launch {
//            when (val response = homeRepository.updateProfile(name, phone)) {
//                is Success -> {
//                    _progress.postValue(false)
//                    _updateResult.value = response.data
//                    Log.e("ERROR", "${response.data.displayName}")
//                }
//                is Failure -> {
//                    _progress.postValue(false)
//                    _error.postValue(response.error)
//                    Log.e("Profile", "${response.error.message}")
//                }
//            }
//        }
//    }
//
//    fun putDeviceId(deviceId: String) {
//        launch {
//            when (val response = homeRepository.putDeviceId(deviceId)) {
//                is Success -> {
//                    _sentToken.value = true
//                    Log.e("DeviceToken", "Success")
//
//                }
//                is Failure -> {
//                    _sentToken.value = false
//                    (response.error is HttpException).let {
//                        if (it) {
//                            if ((response.error as HttpException).code() == HttpURLConnection.HTTP_UNAUTHORIZED) {
//                                _errorCode.value = 401
//                            }
//                        }
//                    }
//
//                    Log.e("DeviceToken", "${response.error.message}")
//                }
//            }
//        }
//    }
//
//    fun deleteDeviceId() {
//        token.addOnCompleteListener {
//            if (it.isSuccessful) {
//                it.result?.let { token ->
//                    deleteId(token)
//                }
//            } else {
//                _logout.value = false
//            }
//        }
//    }
//
//    private fun deleteId(deviceToken: String) {
//        launch {
//            when (val response = homeRepository.deleteDeviceId(deviceToken)) {
//                is Success -> {
//                    _logout.value = true
//
//                }
//                is Failure -> {
//                    _logout.value = false
//                    Log.e("DeviceToken", "${response.error.message}")
//                }
//            }
//        }
//    }
}