package com.pieaksoft.event.consumer.android.ui.base

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext

abstract class BaseVM : ViewModel(), CoroutineScope, KoinComponent {
    val appContext: Context by inject()
    val sp: SharedPreferences by inject()

    internal val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error
    internal val _progress = MutableLiveData<Boolean>()
    val progress: LiveData<Boolean> = _progress

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }
}