package com.pieaksoft.event.consumer.android.views

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.EventInsertType
import nl.joery.timerangepicker.TimeRangePicker

object Dialogs {

    fun showInsertEventDialog(activity: Activity, listener: EventInsertClick) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_insert_event)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<AppCompatButton>(R.id.event_off_btn).setOnClickListener {
            listener.onEventClick(EventInsertType.Off)
        }
        dialog.findViewById<AppCompatButton>(R.id.event_sleep_btn).setOnClickListener {
            listener.onEventClick(EventInsertType.Sleep)
        }
        dialog.findViewById<AppCompatButton>(R.id.event_driving_btn).setOnClickListener {
            listener.onEventClick(EventInsertType.Driving)
        }
        dialog.findViewById<AppCompatButton>(R.id.event_on_btn).setOnClickListener {
            listener.onEventClick(EventInsertType.On)
        }
        dialog.findViewById<AppCompatButton>(R.id.event_cancel_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    interface EventInsertClick {
        fun onEventClick(event: EventInsertType)
    }

    fun showEventTimeRangeDialog(
        activity: Activity,
        listener: TimeRangePicker.OnTimeChangeListener
    ) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_range_picker)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<TimeRangePicker>(R.id.picker)
            .setOnTimeChangeListener(object : TimeRangePicker.OnTimeChangeListener {
                override fun onStartTimeChange(startTime: TimeRangePicker.Time) {
                    listener.onStartTimeChange(startTime)
                    dialog.findViewById<AppCompatTextView>(R.id.start_time).text =
                        startTime.toString()
                    Log.d("TimeRangePicker", "Start time: " + startTime)
                }

                override fun onEndTimeChange(endTime: TimeRangePicker.Time) {
                    listener.onEndTimeChange(endTime)
                    dialog.findViewById<AppCompatTextView>(R.id.end_time).text = endTime.toString()
                    Log.d("TimeRangePicker", "End time: " + endTime.hour)
                }

                override fun onDurationChange(duration: TimeRangePicker.TimeDuration) {
                    listener.onDurationChange(duration)

                    dialog.findViewById<AppCompatTextView>(R.id.duration).text =
                        String.format(
                            activity.getString(R.string.duration), duration.hour.toString(),
                            if (duration.minute.toString() == "0") "00"
                            else duration.minute.toString()
                        )
                    Log.d("TimeRangePicker", "Duration: " + duration.hour)
                }
            })


        dialog.findViewById<AppCompatButton>(R.id.apply_btn).setOnClickListener {

            dialog.dismiss()
        }
        dialog.findViewById<AppCompatButton>(R.id.cancel_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}