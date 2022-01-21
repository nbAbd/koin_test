package com.pieaksoft.event.consumer.android.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.widget.doAfterTextChanged
import com.pieaksoft.event.consumer.android.databinding.ActivityLoginBinding
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.MainActivity
import com.pieaksoft.event.consumer.android.ui.base.BaseActivityNew
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.newIntent
import com.pieaksoft.event.consumer.android.utils.toast
import org.koin.android.viewmodel.ext.android.viewModel


class LoginActivity : BaseActivityNew<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private val loginViewModel: LoginViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()

    private var loginValue: String? = null
    private var passwordValue: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
    }

    override fun setupView() {
        if (profileViewModel.isAuth()) {
            startActivity(MainActivity.newInstance(this@LoginActivity))
        }

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
                startActivity(RegistrationActivity.newInstance(this@LoginActivity))
            }

            loginBtn.setOnClickListener {
                loginViewModel.login(loginValue ?: "", passwordValue ?: "")
            }
        }
    }

    override fun bindViewModel() {
        loginViewModel.isSuccessLogin.observe(this, { success ->
            if (success) {
                startActivity(MainActivity.newInstance(this@LoginActivity))
            }

        })
        loginViewModel.error.observe(this, { message ->
            message?.let {
                toast(ErrorHandler.getErrorMessage(it, this))
            }
        })
    }

    private fun isEnableButton(): Boolean {
        return loginValue != null && passwordValue != null
    }

    companion object {
        fun newInstance(context: Context): Intent {
            return newIntent<LoginActivity>(context).apply {

            }
        }
    }

}