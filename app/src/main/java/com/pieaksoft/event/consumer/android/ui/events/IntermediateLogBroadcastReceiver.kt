package com.pieaksoft.event.consumer.android.ui.events

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.google.gson.Gson
import com.pieaksoft.event.consumer.android.BuildConfig
import com.pieaksoft.event.consumer.android.enums.*
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.utils.*
import java.util.*

class IntermediateLogBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_INTERMEDIATE_LOG_BROADCAST) {
            val event = intent.getByteArrayExtra(EVENT_CODE)?.deserialize() as Event
            val timeZone = intent.getStringExtra(TIMEZONE_ID) ?: Timezone.findByName("").toString()
            val currentDate = Date()

            event.apply {
                eventType = EventInsertType.INTERMEDIATE_LOG.type
                eventCode = EventCode.IMMEDIATE_LOG_WITH_CONVENTIONAL_LOCATION_PRECISION.code
                eventRecordStatus = EventRecordStatusType.ACTIVE.type
                eventRecordOrigin = EventRecordOriginType.AUTOMATICALLY_RECORDED_BY_ELD.type
                date = currentDate.formatToServerDateDefaults(Timezone.findByName(timeZone))
                time = currentDate.formatToServerTimeDefaults(Timezone.findByName(timeZone))
            }
            val builder: Constraints.Builder = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)

            val worker = OneTimeWorkRequest.Builder(IntermediateLogWorker::class.java)
                .addTag("HandleLog")
                .setConstraints(builder.build())

            val data = Data.Builder()
            data.putString(EVENT_CODE, Gson().toJson(event))
            worker.setInputData(data.build())

            WorkManager.getInstance(context).enqueue(worker.build())
        }
    }

    companion object {
        private const val ACTION_INTERMEDIATE_LOG_BROADCAST =
            BuildConfig.APPLICATION_ID + ".ACTION_INTERMEDIATE_LOG"
        const val EVENT_CODE = "event"
        private const val REQUEST_CODE = 0
        const val TIMEZONE_ID = "timezoneId"

        private lateinit var notifyPendingIntent: PendingIntent
        private lateinit var alarmManager: AlarmManager

        @SuppressLint("UnspecifiedImmutableFlag")
        fun startSendingIntermediateLog(
            event: Event,
            activity: Activity,
            firstTrigger: Long,
            timeZoneId: String
        ) {
            activity.apply {
                val notifyIntent = Intent(this, IntermediateLogBroadcastReceiver::class.java)
                notifyIntent.action = ACTION_INTERMEDIATE_LOG_BROADCAST
                notifyIntent.putExtra(EVENT_CODE, event.serialize())
                notifyIntent.putExtra(TIMEZONE_ID, timeZoneId)
                alarmManager = getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
                notifyPendingIntent = PendingIntent.getBroadcast(
                    applicationContext, REQUEST_CODE, notifyIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis().plus(
                        firstTrigger
                    ),
                    60000L,
                    notifyPendingIntent
                )
            }
        }

        fun stopSendingIntermediateLog() {
            if (this::notifyPendingIntent.isInitialized && this::alarmManager.isInitialized) {
                alarmManager.cancel(notifyPendingIntent)
            }
        }
    }
}