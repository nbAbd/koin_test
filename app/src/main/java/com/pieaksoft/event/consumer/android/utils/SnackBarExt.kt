package com.pieaksoft.event.consumer.android.utils

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.CustomToastViewBinding

fun AppCompatActivity.showCustomSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    type: SnackBarType
) {
    val rootView = window.decorView.findViewById(android.R.id.content) as ViewGroup
    val binding = CustomToastViewBinding.bind(
        layoutInflater.inflate(
            R.layout.custom_toast_view,
            rootView,
            false
        )
    )

    binding.title.text = title.toString()
    binding.message.text = message
    binding.verticalLine.background?.colorFilter =
        BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(this@showCustomSnackBar, type.color),
            BlendModeCompat.SRC_IN
        )

    val snackBar = Snackbar.make(rootView, "", duration)
    snackBar.view.setBackgroundColor(Color.TRANSPARENT)
    val snackBarLayout = snackBar.view as Snackbar.SnackbarLayout
    (snackBar.view.layoutParams as FrameLayout.LayoutParams).apply {
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
    }
    snackBarLayout.setPadding(0, 0, 0, 0)
    snackBarLayout.addView(binding.root)
    snackBar.show()
}

enum class SnackBarType(val title: String, @ColorRes val color: Int) {
    ERROR("Error", R.color.toast_red),
    SUCCESS("Success", R.color.toast_green),
    WARNING("Warning", R.color.toast_yellow),
    INFO("Information", R.color.toast_blue)
}