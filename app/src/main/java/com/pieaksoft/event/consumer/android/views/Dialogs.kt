package com.pieaksoft.event.consumer.android.views

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.Window
import androidx.core.content.ContextCompat
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.DialogSwapDriversBinding
import com.pieaksoft.event.consumer.android.utils.addDays
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

    fun showDateTimeSelector(context: Context, listener: (Date) -> Unit) {
        SingleDateAndTimePickerDialog.Builder(context)
            .defaultDate(Date())
            .minDateRange(Date().addDays(-7))
            .maxDateRange(Date())
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
}