package com.pieaksoft.event.consumer.android.ui.events

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pieaksoft.event.consumer.android.ui.events.IntermediateLogHandler.ACTION_INTERMEDIATE_LOG_BROADCAST

class IntermediateLogReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_INTERMEDIATE_LOG_BROADCAST -> {
                IntermediateLogHandler.handleLog(context, intent)
            }
        }
    }
}