package com.pieaksoft.event.consumer.android.views

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.DialogSwapDriversBinding
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.enums.Timezone
import com.pieaksoft.event.consumer.android.ui.events.InsertEventFragment
import com.pieaksoft.event.consumer.android.utils.USER_TIMEZONE
import com.pieaksoft.event.consumer.android.utils.addDays
import java.text.SimpleDateFormat
import java.time.*
import java.util.*

object Dialogs {
    fun showSwapDriversDialog(activity: Activity, swapCallback: () -> Unit) {
        val dialogSwapDriversBinding =
            DialogSwapDriversBinding.inflate(LayoutInflater.from(activity))
        val dialog = Dialog(activity).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(false)
            setContentView(dialogSwapDriversBinding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        dialogSwapDriversBinding.apply {
            swapConfirmBtn.setOnClickListener {
                swapCallback()
                dialog.dismiss()
            }

            swapCancelBtn.setOnClickListener {
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    fun showDateTimeSelector(context: Context, sp: SharedPreferences, listener: (Date) -> Unit) {
        val timezoneId =
            Timezone.findByName(timezone = sp.getString(USER_TIMEZONE, null) ?: "")

        val zoneId = ZoneId.of(timezoneId.value)
        val timezone = TimeZone.getTimeZone(zoneId.id)
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        val dateSdf = sdf.parse(LocalDateTime.now(zoneId).toString()) ?: Date()

        SingleDateAndTimePickerDialog.Builder(context)
            .customLocale(Locale.US)
            .setTimeZone(timezone)
            .defaultDate(dateSdf)
            .minDateRange(dateSdf.addDays(-7))
            .maxDateRange(dateSdf)
            .minutesStep(1)
            .bottomSheet()
            .curved()
            .backgroundColor(ContextCompat.getColor(context, R.color.grey))
            .mainColor(ContextCompat.getColor(context, R.color.white))
            .titleTextColor(ContextCompat.getColor(context, R.color.white))
            .title(context.getString(R.string.choose_date_time))
            .listener { listener(it) }
            .display()
    }

    fun showInsertEventDialogFragment(
        fragmentManager: FragmentManager,
        onCancelled: (IsCancelled: Boolean) -> Unit,
        eventDutyStatus: EventCode
    ) {
        val dialog = InsertEventFragment(onCancelled,eventDutyStatus)
        dialog.show(fragmentManager, InsertEventFragment::class.java.name)
    }
}