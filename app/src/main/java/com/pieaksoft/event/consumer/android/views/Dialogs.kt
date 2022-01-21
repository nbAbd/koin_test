package com.pieaksoft.event.consumer.android.views

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Window
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.EventInsertCode
import com.pieaksoft.event.consumer.android.model.EventInsertType
import com.pieaksoft.event.consumer.android.utils.addDays
import nl.joery.timerangepicker.TimeRangePicker
import java.util.*

object Dialogs {

    fun showInsertEventDialog(activity: Activity, listener: EventInsertClick) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_insert_event)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.findViewById<AppCompatButton>(R.id.event_off_btn).setOnClickListener {
            listener.onEventClick(EventInsertCode.Off)
            dialog.dismiss()
        }
        dialog.findViewById<AppCompatButton>(R.id.event_sleep_btn).setOnClickListener {
            listener.onEventClick(EventInsertCode.Sleep)
            dialog.dismiss()
        }
        dialog.findViewById<AppCompatButton>(R.id.event_driving_btn).setOnClickListener {
            listener.onEventClick(EventInsertCode.Driving)
            dialog.dismiss()
        }
        dialog.findViewById<AppCompatButton>(R.id.event_on_btn).setOnClickListener {
            listener.onEventClick(EventInsertCode.On)
            dialog.dismiss()
        }
        dialog.findViewById<AppCompatButton>(R.id.event_cancel_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showSwapDriversDialog(activity: Activity, listener: SwapDriversListener) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_swap_drivers)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.findViewById<AppCompatButton>(R.id.swap_confirm_btn).setOnClickListener {
            listener.onSwapDriversClick()
            dialog.dismiss()
        }
        dialog.findViewById<AppCompatButton>(R.id.swap_cancel_btn).setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    interface SwapDriversListener {
        fun onSwapDriversClick()
    }

    interface EventInsertClick {
        fun onEventClick(event: EventInsertCode)
    }

    fun showEventTimeRangeDialog(
        activity: Activity,
        listener: TimeRangePicker.OnTimeChangeListener
    ) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
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


    fun  showDateTimeSelector(context: Context, listener: DateSelectListener){
        SingleDateAndTimePickerDialog.Builder(context)
            .minDateRange(Date().addDays(-7))
            .maxDateRange(Date())
            .bottomSheet()
            .curved()
            .backgroundColor(ContextCompat.getColor(context, R.color.grey))
            .mainColor(ContextCompat.getColor(context, R.color.white))
            .titleTextColor(ContextCompat.getColor(context, R.color.white))
            .title(context.getString(R.string.choose_date_time))
            .listener {date ->
                listener.onDateSelect(date)
            }
            .display()
    }

    interface DateSelectListener {
        fun onDateSelect(date: Date)
    }
}