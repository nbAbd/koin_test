package com.pieaksoft.event.consumer.android.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import by.kirich1409.viewbindingdelegate.viewBinding
import com.facebook.drawee.view.SimpleDraweeView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.ItemDriverBinding
import com.pieaksoft.event.consumer.android.model.ProfileModel
import com.pieaksoft.event.consumer.android.utils.setImageWithPlaceHolder
import com.pieaksoft.event.consumer.android.utils.visible

class DriverCardView(context: Context, attrs: AttributeSet): ConstraintLayout(context, attrs) {
    private val binding by viewBinding(ItemDriverBinding::bind)

    init {
        inflate(context, R.layout.item_driver, this)

        val imageView: SimpleDraweeView = findViewById(R.id.driver_image)
        val textView: AppCompatTextView = findViewById(R.id.driver_name)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DriverCardView)
        binding.driverImage.setImageWithPlaceHolder(attributes.getString(R.styleable.DriverCardView_image), attributes.getString(R.styleable.DriverCardView_name)?:"")
        textView.text = attributes.getString(R.styleable.DriverCardView_name)
        attributes.recycle()

    }

    fun setDriverInfo(driver: ProfileModel){
        with(binding){
            driverName.text = String.format("%s %s", driver.user?.firstName, driver.user?.lastName)
            loginValue.text = driver.user?.email
            loginValue.text = driver.user?.email
            companyValue.text = driver.company?.name
            locationValue.text = driver.company?.state
        }
    }
    fun setEmpty(empty: Boolean){
        with(binding){
            driverState.visible(!empty, false)
            emptyState.visible(empty, false)
        }

    }
}