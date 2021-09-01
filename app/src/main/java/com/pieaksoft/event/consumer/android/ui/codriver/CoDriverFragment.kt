package com.pieaksoft.event.consumer.android.ui.codriver

import androidx.core.widget.addTextChangedListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pieaksoft.event.consumer.android.ui.base.BaseFragment
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentCoDriverBinding
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.login.LoginVM
import com.pieaksoft.event.consumer.android.ui.profile.ProfileVM
import com.pieaksoft.event.consumer.android.utils.*
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel


class CoDriverFragment : BaseFragment(R.layout.fragment_co_driver) {
    private val binding by viewBinding(FragmentCoDriverBinding::bind)
    private val profileVM: ProfileVM by viewModel()
    private val loginVM: LoginVM by viewModel()

    private var loginValue: String? = null
    private var passwordValue: String? = null

    override fun setViews() {
        profileVM.getProfile()
        if (isCooDriverExist()) {
            profileVM.getProfile(true)
        }

        with(binding) {
            driver2.setEmpty(true)
            driver2.setOnClickListener {
                driversContainer.hide()
                loginContainer.show()
            }

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


            loginBtn.setOnClickListener {
                loginVM.login(loginValue ?: "", passwordValue ?: "", true)
            }

            cancelBtn.setOnClickListener {
                driversContainer.show()
                loginContainer.hide()
            }
        }
    }

    override fun bindVM() {
        profileVM.driver1.observe(this, {
            launch {
                binding.driver1.setDriverInfo(it)
            }
        })
        profileVM.driver2.observe(this, {
            launch {
                binding.driver2.setDriverInfo(it)
            }
        })

        profileVM.error.observe(this, { message ->
            message?.let {
                toast(ErrorHandler.getErrorMessage(it, requireContext()))
            }
        })

        loginVM.isSuccessLogin.observe(this, { success ->
            if (success) {

            }
        })

    }

    private fun isEnableButton(): Boolean {
        return loginValue != null && passwordValue != null
    }

    private fun isCooDriverExist(): Boolean {
        return sp.getString(SHARED_PREFERENCES_ADDITIONAL_USER_ID, "") != ""
    }
}