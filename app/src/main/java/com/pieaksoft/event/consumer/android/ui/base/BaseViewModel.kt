package com.pieaksoft.event.consumer.android.ui.base

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pieaksoft.event.consumer.android.enums.Timezone
import com.pieaksoft.event.consumer.android.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel(context: Application) : AndroidViewModel(context), CoroutineScope,
    KoinComponent {
    val appContext: Context by inject()
    val sp: SharedPreferences by inject()

    internal val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error
    private val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = _progress

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun showProgress() {
        if (isNetworkAvailable) {
            _progress.postValue(true)
        }
    }

    val context: Context
        get() = getApplication()

    val isNetworkAvailable: Boolean
        get() = context.isNetworkAvailable()

    fun hideProgress() {
        _progress.postValue(false)
    }

    fun getUserTimezone(): Timezone {
        return Timezone.findByName(sp.getString(USER_TIMEZONE, "")!!)
    }

    fun getFormattedUserDate(configTimezone: Boolean = true): String {
        return Date().formatToServerDateDefaults(if (configTimezone) getUserTimezone() else null)
    }

    fun getFormattedUserTime(configTimezone: Boolean = true): String {
        return Date().formatToServerTimeDefaults(if (configTimezone) getUserTimezone() else null)
    }

    fun saveUserTimezone(timezone: String?) {
        sp.put(USER_TIMEZONE, timezone)
    }
}