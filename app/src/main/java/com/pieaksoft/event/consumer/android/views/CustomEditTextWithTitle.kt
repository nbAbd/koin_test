package com.pieaksoft.event.consumer.android.views

import android.content.Context
import android.content.res.TypedArray
import android.text.InputFilter
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.utils.afterTextChanged

private const val DEFAULT_INPUT_HEIGHT = 48F
private const val defStyle = 0

class FloatingTitleEditText constructor(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {
    private val title: AppCompatTextView
    private val editText: AppCompatEditText

    init {
        inflate(context, R.layout.custom_floating_title_edittext, this).also {
            title = it.findViewById(R.id.title)
            editText = it.findViewById(R.id.edit_text)
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.FloatingTitleEditText,
                defStyle,
                defStyle
            ).run { initProperties(this); recycle() }
        }
    }

    private fun initProperties(typedArray: TypedArray) = with(typedArray) {
        getString(R.styleable.FloatingTitleEditText_title).also { title.text = it }
        getString(R.styleable.FloatingTitleEditText_hint).also { editText.hint = it }
        getDimension(R.styleable.FloatingTitleEditText_minimumHeight, DEFAULT_INPUT_HEIGHT).also {
            (editText.layoutParams as MarginLayoutParams).apply {
                height = if (it > DEFAULT_INPUT_HEIGHT) {
                    editText.gravity = Gravity.TOP or Gravity.START
                    it.toInt()
                } else {
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        it,
                        context.resources.displayMetrics
                    ).toInt()
                }
            }
        }
        getInteger(R.styleable.FloatingTitleEditText_maxElements, Int.MAX_VALUE).also {
            editText.filters = arrayOf(InputFilter.LengthFilter(it))
        }
        getDrawable(R.styleable.FloatingTitleEditText_android_drawableEnd).also {
            editText.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                it,
                null
            )
        }

        getBoolean(R.styleable.FloatingTitleEditText_commentField, false).also {
            editText.afterTextChanged { comment ->
                title.text =
                    context.getString(R.string.comment_title_din, comment.length.toString())
            }
        }
    }
}