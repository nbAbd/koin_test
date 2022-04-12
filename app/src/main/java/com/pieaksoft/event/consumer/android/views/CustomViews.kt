package com.pieaksoft.event.consumer.android.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.AutoCompleteTextView
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.utils.afterTextChanged

private const val DEFAULT_INPUT_HEIGHT = 48F
private const val defStyle = 0

class FloatingTitleEditText constructor(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {
    val title: AppCompatTextView
    val editText: AppCompatEditText

    init {
        inflate(context, R.layout.custom_floating_title_edittext, this).also {
            title = it.findViewById(R.id.title)
            editText = it.findViewById(R.id.edit_text)
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.FloatingTitleEditText,
                defStyle,
                defStyle
            ).run {
                initProperties(this)
                recycle()
            }
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
            if (it) {
                editText.afterTextChanged { comment ->
                    title.text =
                        context.getString(R.string.comment_title_din, comment.length.toString())
                }
            }
        }

        getBoolean(R.styleable.FloatingTitleEditText_editable, true).also {
            if (it.not()) {
                editText.inputType = InputType.TYPE_NULL
                editText.isCursorVisible = false
                editText.keyListener = null
            }
        }
    }
}


class DropdownSpinnerWithTitle(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    private val title: AppCompatTextView
    private val spinner: AppCompatAutoCompleteTextView

    init {
        inflate(context, R.layout.custom_spinner_with_title, this).also {
            title = it.findViewById(R.id.title)
            spinner = it.findViewById(R.id.dropdown_spinner)
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.DropdownSpinnerWithTitle,
                defStyle,
                defStyle
            ).run {
                initProperties(this)
                recycle()
            }
        }
    }

    private fun initProperties(typedArray: TypedArray) = with(typedArray) {
        spinner.avoidDropdownFocus()
        spinner.keyListener = null
        spinner.isCursorVisible = false

        getString(R.styleable.DropdownSpinnerWithTitle_title).also { title.text = it }
        getDimension(
            R.styleable.DropdownSpinnerWithTitle_minimumHeight,
            DEFAULT_INPUT_HEIGHT
        ).also {
            (spinner.layoutParams as MarginLayoutParams).apply {
                height = if (it > DEFAULT_INPUT_HEIGHT) {
                    it.toInt().dp
                } else {
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        it,
                        context.resources.displayMetrics
                    ).toInt()
                }
            }
        }
    }

    fun getSpinner() = spinner
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

@SuppressLint("DiscouragedPrivateApi")
fun AutoCompleteTextView.avoidDropdownFocus() {
    try {
        val isAppCompat = this is AppCompatAutoCompleteTextView
        val autoCompleteTextViewClass =
            if (isAppCompat) AppCompatAutoCompleteTextView::class.java else AutoCompleteTextView::class.java
        val popupWindowClass =
            if (isAppCompat) androidx.appcompat.widget.ListPopupWindow::class.java else android.widget.ListPopupWindow::class.java

        val listPopup = autoCompleteTextViewClass
            .getDeclaredField("mPopup")
            .apply { isAccessible = true }
            .get(this)
        if (popupWindowClass.isInstance(listPopup)) {
            val popup = popupWindowClass
                .getDeclaredField("mPopup")
                .apply { isAccessible = true }
                .get(listPopup)
            if (popup is PopupWindow) {
                popup.isFocusable = false
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}