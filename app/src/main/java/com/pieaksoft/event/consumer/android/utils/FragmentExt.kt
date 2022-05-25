package com.pieaksoft.event.consumer.android.utils

import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.pieaksoft.event.consumer.android.enums.EventCode
import com.pieaksoft.event.consumer.android.ui.activities.main.MainActivity

fun DialogFragment.hideSystemUI() {
    dialog?.window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    )
}

fun Fragment.isFragmentVisible(): Boolean {
    return activity != null && isVisible && !isRemoving
}

fun <T> T.performStatusChange(to: EventCode = EventManager.events.lastItemEventCode) where T : Modifier, T : Fragment {
    (requireActivity() as MainActivity).navigateTo(status = to)
}