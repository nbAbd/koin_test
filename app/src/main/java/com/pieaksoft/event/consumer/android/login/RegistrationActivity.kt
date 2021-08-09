package com.pieaksoft.event.consumer.android.login

import android.content.Context
import android.content.Intent
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.base.BaseActivity
import com.pieaksoft.event.consumer.android.utils.newIntent

class RegistrationActivity: BaseActivity(R.layout.activity_registration)  {
    override fun setView() {

    }

    companion object {
        fun newInstance(context: Context): Intent {
            return newIntent<RegistrationActivity>(context).apply {

            }
        }
    }
}