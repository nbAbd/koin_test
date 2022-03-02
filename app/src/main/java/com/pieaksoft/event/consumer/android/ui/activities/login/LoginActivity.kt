package com.pieaksoft.event.consumer.android.ui.activities.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import com.pieaksoft.event.consumer.android.databinding.ActivityLoginBinding
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.activities.main.MainActivity
import com.pieaksoft.event.consumer.android.ui.activities.registration.RegistrationActivity
import com.pieaksoft.event.consumer.android.ui.base.BaseActivityNew
import com.pieaksoft.event.consumer.android.utils.hideKeyboard
import com.pieaksoft.event.consumer.android.utils.newIntent
import com.pieaksoft.event.consumer.android.utils.toast
import org.koin.android.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivityNew<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModel()

    private var loginValue: String? = null
    private var passwordValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
    }

    override fun setupView() {
        with(binding) {
            loginName.doAfterTextChanged {
                if (it?.length == 0) {
                    loginValue = null
                    loginBtn.isEnabled = isEnableButton()
                } else {
                    loginValue = it.toString()
                    loginBtn.isEnabled = isEnableButton()
                }
            }

            password.doAfterTextChanged {
                if (it?.length == 0) {
                    passwordValue = null
                    loginBtn.isEnabled = isEnableButton()
                } else {
                    passwordValue = it.toString()
                    loginBtn.isEnabled = isEnableButton()
                }
            }

            registerBtn.setOnClickListener {
                hideKeyboard()
                startActivity(RegistrationActivity.newInstance(this@LoginActivity))
            }

            loginBtn.setOnClickListener {
                hideKeyboard()
                loginViewModel.login(loginValue ?: "", passwordValue ?: "")
            }
        }
    }

    override fun bindViewModel() {
        loginViewModel.isSuccessLogin.observe(this, { success ->
            val (isSuccess, errorMessage) = success
            when {
                isSuccess -> startActivity(MainActivity.newInstance(this@LoginActivity))
                errorMessage != null -> toast(getString(errorMessage))
            }
        })
        loginViewModel.error.observe(this, { message ->
            message?.let {
                toast(ErrorHandler.getErrorMessage(it, this))
            }
        })

        loginViewModel.progress.observe(this, {
            setProgressVisible(it)
        })
    }

    private fun isEnableButton() = loginValue != null && passwordValue != null

    companion object {
        fun newInstance(context: Context): Intent {
            return newIntent<LoginActivity>(context)
        }
    }
}