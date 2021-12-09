package com.pieaksoft.event.consumer.android.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.ItemEditTextBinding

class CustomEditTextWithTitle(context: Context, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {

    private val binding by viewBinding(ItemEditTextBinding::bind)

    init {
        inflate(context, R.layout.item_edit_text, this)

        val title: AppCompatTextView = findViewById(R.id.title)
        val editText: AppCompatEditText = findViewById(R.id.edit_text)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.CustomEditTextWithTitle)
        binding.title.text = attributes.getString(R.styleable.CustomEditTextWithTitle_title)
        binding.title.isAllCaps = attributes.getBoolean(R.styleable.CustomEditTextWithTitle_text_all_caps, false)
        binding.editText.hint = attributes.getString(R.styleable.CustomEditTextWithTitle_hint)
        attributes.recycle()

    }

    fun getValue(): String {
        return binding.editText.text.toString()
    }
}