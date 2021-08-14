package com.pieaksoft.event.consumer.android.ui.login

import com.pieaksoft.event.consumer.android.MainActivity
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: BaseActivity(R.layout.activity_login) {
    override fun setView() {
        register_btn.setOnClickListener {
           // startActivity(RegistrationActivity.newInstance(this))
            startActivity(MainActivity.newInstance(this))
        }
    }
}