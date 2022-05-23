package com.pieaksoft.event.consumer.android.views

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.facebook.drawee.view.SimpleDraweeView
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.ItemDriverBinding
import com.pieaksoft.event.consumer.android.model.profile.Profile
import com.pieaksoft.event.consumer.android.utils.setImageWithPlaceHolder
import com.pieaksoft.event.consumer.android.utils.visible

class DriverCardView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    var binding: ItemDriverBinding

    init {
        inflate(context, R.layout.item_driver, this).also {
            binding = ItemDriverBinding.bind(it)
        }

        val imageView: SimpleDraweeView = findViewById(R.id.driver_image)
        val textView: AppCompatTextView = findViewById(R.id.driver_name)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.DriverCardView)
        binding.driverImage.setImageWithPlaceHolder(
            attributes.getString(R.styleable.DriverCardView_image),
            attributes.getString(R.styleable.DriverCardView_name) ?: ""
        )
        textView.text = attributes.getString(R.styleable.DriverCardView_name)
        attributes.recycle()
    }

    fun setDriverInfo(driver: Profile, current: Boolean = true) {
        with(binding) {
            driverName.text = String.format("%s %s", driver.user.firstName, driver.user.lastName)
            loginValue.text = driver.user.email
            companyValue.text = driver.company?.name
            locationValue.text = driver.company?.state
            if (current) {
                driverStatus.text = context.getString(R.string.current_driver)
                driverStatus.background.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    ), PorterDuff.Mode.SRC_IN
                )
            } else {
                driverStatus.text = context.getString(R.string.co_driver)
                driverStatus.background.colorFilter = PorterDuffColorFilter(
                    ContextCompat.getColor(
                        context,
                        R.color.secondary_gray
                    ), PorterDuff.Mode.SRC_IN
                )
            }
        }
    }

    fun setEmpty(empty: Boolean) {
        with(binding) {
            driverState.visible(!empty, false)
            emptyState.visible(empty, false)
        }
    }
}