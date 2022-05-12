package com.pieaksoft.event.consumer.android.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


/**
 * Removes all previous observers from a view model, so it is only observed once
 *
 * @param [lifecycleOwner] The current LifecycleOwner of the observable
 * @param [observer] The observer that needs to be removed
 */
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}