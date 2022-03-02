package com.pieaksoft.event.consumer.android.ui.activities.registration

import android.content.Context
import android.content.Intent
import android.widget.ArrayAdapter
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.ui.base.BaseActivity
import com.pieaksoft.event.consumer.android.utils.newIntent

class RegistrationActivity: BaseActivity(R.layout.activity_registration)  {
    override fun setView() {

        val countries = resources.getStringArray(R.array.countries)
        val adapter = ArrayAdapter(this, R.layout.drop_down_item, countries)
      //  location_value.setAdapter(adapter)
    }

    override fun bindVM() {

    }

    companion object {
        fun newInstance(context: Context): Intent {
            return newIntent<RegistrationActivity>(context).apply {

            }
        }
    }
}