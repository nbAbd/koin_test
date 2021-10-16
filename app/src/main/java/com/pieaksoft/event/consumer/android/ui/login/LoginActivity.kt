package com.pieaksoft.event.consumer.android.ui.login

import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pieaksoft.event.consumer.android.MainActivity
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.ActivityLoginBinding
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.base.BaseActivity
import com.pieaksoft.event.consumer.android.ui.profile.ProfileVM
import com.pieaksoft.event.consumer.android.utils.toast
import org.koin.android.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivity(R.layout.activity_login) {
    private val loginVM: LoginVM by viewModel()
    private val profileVM: ProfileVM by viewModel()
    private val binding by viewBinding(ActivityLoginBinding::bind)

    private var loginValue: String? = null
    private var passwordValue: String? = null

    override fun setView() {
        if(profileVM.isAuth()){
            startActivity(MainActivity.newInstance(this@LoginActivity))
        }

        with(binding) {
            loginName.addTextChangedListener {
                if (it?.length == 0) {
                    loginValue = null
                    loginBtn.isEnabled = isEnableButton()
                } else {
                    loginValue = it.toString()
                    loginBtn.isEnabled = isEnableButton()
                }
            }

            password.addTextChangedListener {
                if (it?.length == 0) {
                    passwordValue = null
                    loginBtn.isEnabled = isEnableButton()
                } else {
                    passwordValue = it.toString()
                    loginBtn.isEnabled = isEnableButton()
                }
            }

            registerBtn.setOnClickListener {
                startActivity(RegistrationActivity.newInstance(this@LoginActivity))
            }

            loginBtn.setOnClickListener {
                loginVM.login(loginValue?:"", passwordValue?:"")
            }
        }
    }

    override fun bindVM() {
        loginVM.isSuccessLogin.observe(this, { success ->
            if (success) {
                startActivity(MainActivity.newInstance(this@LoginActivity))
            }

        })
        loginVM.error.observe(this, Observer { message ->
            message?.let {
                toast(ErrorHandler.getErrorMessage(it, this))
            }
        })
    }

    private fun isEnableButton(): Boolean {
        return loginValue != null && passwordValue != null
    }

}