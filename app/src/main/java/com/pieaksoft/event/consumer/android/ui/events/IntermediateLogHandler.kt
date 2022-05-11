package com.pieaksoft.event.consumer.android.ui.events

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import com.google.gson.Gson
import com.pieaksoft.event.consumer.android.BuildConfig
import com.pieaksoft.event.consumer.android.enums.*
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.utils.deserialize
import com.pieaksoft.event.consumer.android.utils.formatToServerDateDefaults
import com.pieaksoft.event.consumer.android.utils.formatToServerTimeDefaults
import com.pieaksoft.event.consumer.android.utils.serialize
import java.util.*

object IntermediateLogHandler {

    const val ACTION_INTERMEDIATE_LOG_BROADCAST =
        BuildConfig.APPLICATION_ID + ".ACTION_INTERMEDIATE_LOG"
    const val EVENT_CODE = "event"
    private const val REQUEST_CODE = 0
    private const val TIMEZONE_ID = "timezoneId"

    private lateinit var notifyPendingIntent: PendingIntent
    private lateinit var alarmManager: AlarmManager


    fun startSendingIntermediateLog(
        event: Event,
        activity: Activity,
        firstTrigger: Long,
        timeZoneId: String,
    ) {
        activity.apply {
            createPendingIntent(activity, event, timeZoneId)
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis().plus(
                    firstTrigger
                ),
                AlarmManager.INTERVAL_HOUR,
                notifyPendingIntent
            )
        }
    }

    fun stopSendingIntermediateLog() {
        if (this::notifyPendingIntent.isInitialized && this::alarmManager.isInitialized) {
            alarmManager.cancel(notifyPendingIntent)
        }
    }

    private fun createPendingIntent(activity: Activity, event: Event, timeZoneId: String) {
        activity.apply {
            val notifyIntent = Intent(this, IntermediateLogReceiver::class.java)
            notifyIntent.action = ACTION_INTERMEDIATE_LOG_BROADCAST
            notifyIntent.putExtra(EVENT_CODE, event.serialize())
            notifyIntent.putExtra(TIMEZONE_ID, timeZoneId)
            alarmManager = getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            notifyPendingIntent = PendingIntent.getBroadcast(
                applicationContext, REQUEST_CODE, notifyIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }


    @SuppressLint("UnspecifiedImmutableFlag")
    fun checkForAlarmSet(context: Context): Boolean {
        return PendingIntent.getBroadcast(
            context, REQUEST_CODE,
            Intent(ACTION_INTERMEDIATE_LOG_BROADCAST),
            PendingIntent.FLAG_NO_CREATE
        ) != null
    }

    fun handleLog(context: Context, intent: Intent) {
        val event = intent.getByteArrayExtra(EVENT_CODE)?.deserialize() as Event
        val timeZone =
            intent.getStringExtra(TIMEZONE_ID) ?: Timezone.findByName("").toString()
        val currentDate = Date()

        fillUpIntermediateEvent(event)
        event.apply {
            date = currentDate.formatToServerDateDefaults(Timezone.findByName(timeZone))
            time = currentDate.formatToServerTimeDefaults(Timezone.findByName(timeZone))
        }
        val builder: Constraints.Builder = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)

        val worker = OneTimeWorkRequest.Builder(IntermediateLogWorker::class.java)
            .setConstraints(builder.build())

        val data = Data.Builder()
        data.putString(EVENT_CODE, Gson().toJson(event))
        worker.setInputData(data.build())

        WorkManager.getInstance(context).enqueue(worker.build())
    }

    fun fillUpIntermediateEvent(event: Event) {
        event.apply {
            eventType = EventInsertType.INTERMEDIATE_LOG.type
            eventCode = EventCode.INTERMEDIATE_LOG_WITH_CONVENTIONAL_LOCATION_PRECISION.code
            eventRecordStatus = EventRecordStatusType.ACTIVE.type
            eventRecordOrigin = EventRecordOriginType.AUTOMATICALLY_RECORDED_BY_ELD.type
        }
    }
}