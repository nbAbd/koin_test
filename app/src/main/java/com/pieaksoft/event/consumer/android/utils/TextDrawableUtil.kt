package com.pieaksoft.event.consumer.android.utils
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface

import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.pieaksoft.event.consumer.android.R


object TextDrawableUtil {
    fun getTextDrawable(context: Context, text: String?): TextDrawable {
        var text = text
        if (text == null) text = ""
        val generator = ColorGenerator.MATERIAL
        val color = generator.getColor(text)

        val builder = TextDrawable.builder()
            .beginConfig()
            .textColor(Color.WHITE)
            .useFont(Typeface.DEFAULT)
            .fontSize(context.resources
                .getDimensionPixelSize(R.dimen.text_drawable_avatar_size))
            .bold()
            .toUpperCase()
            .endConfig()
            .round()
        var letters = ""
        for (s in text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            if (s.length > 0 && letters.length < 2) letters += s.substring(0, 1)

        val drawable = builder.build(letters, color)
        drawable.setPadding(Rect())
        return drawable
    }
}