package com.pieaksoft.event.consumer.android.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.facebook.drawee.view.SimpleDraweeView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.utils.hide
import com.pieaksoft.event.consumer.android.utils.setImageWithPlaceHolder
import com.pieaksoft.event.consumer.android.utils.show
import com.pieaksoft.event.consumer.android.utils.visible
import kotlinx.android.synthetic.main.item_driver.view.*

class DriverCardView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {
    init {
        inflate(context, R.layout.item_driver, this)

        val imageView: SimpleDraweeView = findViewById(R.id.driver_image)
        val textView: AppCompatTextView = findViewById(R.id.driver_name)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DriverCardView)
        imageView.setImageWithPlaceHolder(attributes.getString(R.styleable.DriverCardView_image), attributes.getString(R.styleable.DriverCardView_name)?:"")
        textView.text = attributes.getString(R.styleable.DriverCardView_name)
        attributes.recycle()

    }

    fun setEmpty(empty: Boolean){
        driver_state.visible(!empty, false)
        empty_state.visible(empty, false)
    }
}