package com.pieaksoft.event.consumer.android.views

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import androidx.appcompat.widget.AppCompatButton
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.model.EventInsertType

object Dialogs {

    fun showInsertEventDialog(activity: Activity, listener: EventInsertClick){
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.insert_event_dialog)
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

    interface EventInsertClick{
        fun onEventClick(event: EventInsertType)
    }

}