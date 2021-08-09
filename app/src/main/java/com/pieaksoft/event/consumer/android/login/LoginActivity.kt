package com.pieaksoft.event.consumer.android.login

import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: BaseActivity(R.layout.activity_login) {
    override fun setView() {
        register_btn.setOnClickListener {
            startActivity(RegistrationActivity.newInstance(this))
        }
    }
}