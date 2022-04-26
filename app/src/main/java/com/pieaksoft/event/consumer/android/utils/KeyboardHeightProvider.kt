package com.pieaksoft.event.consumer.android.utils

import android.content.Context
import android.graphics.Rect
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.LinearLayout
import android.view.ViewGroup
import android.util.DisplayMetrics
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View

class KeyboardHeightProvider(
    context: Context,
    windowManager: WindowManager,
    parentView: View,
    listener: (keyBoardHeight: Int) -> Unit
) : PopupWindow(context) {

    init {
        val popupView = LinearLayout(context)
        popupView.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        popupView.viewTreeObserver.addOnGlobalLayoutListener {
            val metrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(metrics)
            val rect = Rect()
            popupView.getWindowVisibleDisplayFrame(rect)
            var keyboardHeight = metrics.heightPixels - (rect.bottom - rect.top)
            val resourceID =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceID > 0) {
                keyboardHeight -= context.resources.getDimensionPixelSize(resourceID)
            }
            if (keyboardHeight < 100) {
                keyboardHeight = 0
            }
            listener(keyboardHeight)
        }
        contentView = popupView
        softInputMode =
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(0))
        parentView.post { showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0) }
    }
}