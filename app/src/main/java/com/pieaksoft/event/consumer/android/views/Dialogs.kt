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
import com.pieaksoft.event.consumer.android.enums.Timezone
import com.pieaksoft.event.consumer.android.model.event.Event
import com.pieaksoft.event.consumer.android.ui.events.InsertEventFragment
import com.pieaksoft.event.consumer.android.utils.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

object Dialogs {
    fun showSwapDriversDialog(activity: Activity, swapCallback: (then:()->Unit) -> Unit) {
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
                swapCallback{
                    dialog.dismiss()
                }
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
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
        val dateSdf = sdf.parse(LocalDateTime.now(zoneId).toString()) ?: Date()

        val startDate = EventManager.events.lastItemStartDate?.addMinutes(1) ?: dateSdf

        SingleDateAndTimePickerDialog.Builder(context)
            .customLocale(Locale.US)
            .defaultDate(dateSdf)
            .minDateRange(startDate)
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
        event: Event? = null,
        onCancelled: (Boolean) -> Unit = {}
    ) {
        val dialog = InsertEventFragment(event, onCancelled)
        dialog.show(fragmentManager, InsertEventFragment::class.java.name)
    }
}