package com.pieaksoft.event.consumer.android.utils

import android.view.WindowManager
import androidx.fragment.app.DialogFragment

fun DialogFragment.hideSystemUI() {
    dialog?.window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    )
}