package com.pieaksoft.event.consumer.android.utils.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.pieaksoft.event.consumer.android.utils.put
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PhoneRebootReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        const val IS_PHONE_REBOOTED = "is_phone_rebooted"
    }

    private val sp: SharedPreferences by inject()

    override fun onReceive(p0: Context?, p1: Intent?) {
        sp.put(IS_PHONE_REBOOTED, true)
    }
}