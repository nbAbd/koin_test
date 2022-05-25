package com.pieaksoft.event.consumer.android.ui.codriver

import android.content.Intent
import androidx.core.widget.addTextChangedListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pieaksoft.event.consumer.android.R
import com.pieaksoft.event.consumer.android.databinding.FragmentCoDriverBinding
import com.pieaksoft.event.consumer.android.network.ErrorHandler
import com.pieaksoft.event.consumer.android.ui.activities.login.LoginViewModel
import com.pieaksoft.event.consumer.android.ui.activities.main.MainActivity
import com.pieaksoft.event.consumer.android.ui.base.BaseMVVMFragment
import com.pieaksoft.event.consumer.android.ui.profile.ProfileViewModel
import com.pieaksoft.event.consumer.android.utils.*
import com.pieaksoft.event.consumer.android.views.Dialogs
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.viewmodel.ext.android.viewModel


class CoDriverFragment : BaseMVVMFragment<FragmentCoDriverBinding, ProfileViewModel>(), Modifier {
    override val viewModel: ProfileViewModel by viewModel()
    private val loginViewModel: LoginViewModel by viewModel()

    private var loginValue: String? = null
    private var passwordValue: String? = null

    override fun setupView() {
        viewModel.getDriversInfo()

        with(binding) {
            additionalDriver.setEmpty(true)
            additionalDriver.setOnClickListener {
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
                loginViewModel.login(loginValue ?: "", passwordValue ?: "", true)
            }

            cancelBtn.setOnClickListener {
                driversContainer.show()
                loginContainer.hide()
            }

            btnExit.setOnClickListener { performStatusChange() }
        }
    }

    private fun isEnableButton(): Boolean {
        return loginValue != null && passwordValue != null
    }

    override fun observe() {
        loginViewModel.progress.observe(this) {
            (activity as MainActivity).setProgressVisible(it)
        }

        loginViewModel.error.observe(this) { message ->
            message?.let {
                toast(ErrorHandler.getErrorMessage(it, requireContext()))
            }
        }

        loginViewModel.isSuccessLogin.observe(this) { success ->
            val (isSuccess, errorMessage) = success
            when {
                isSuccess -> {
                    launch {
                        binding.loginContainer.hide()
                        binding.driversContainer.show()
                        viewModel.getDriversInfo()
                    }
                }
                errorMessage != null -> {
                    toast(getString(errorMessage))
                }
            }
        }

        viewModel.primaryDriver.observe(this) {
            it?.let {
                launch {
                    binding.primaryDriver.setDriverInfo(it)
                }
            }
        }

        viewModel.additionalDriver.observe(this) {
            it?.let {
                launch {
                    binding.additionalDriver.setDriverInfo(it, false)
                    binding.additionalDriver.setEmpty(false)
                    binding.additionalDriver.setOnClickListener {
                        Dialogs.showSwapDriversDialog(requireActivity()) {

                            // Sends logout event for current driver before swapping drivers
                            loginViewModel.sendLogoutEvent {
                                viewModel.swapDrivers()
                            }
                        }
                    }
                }
            }
        }

        viewModel.needUpdateObservable.observe(this) {
            viewModel.getDriversInfo()

            // Sends login event for current driver(which was co-driver before swapping) after swapping drivers
            loginViewModel.sendLoginEvent()

            LocalBroadcastManager
                .getInstance(requireContext())
                .sendBroadcast(Intent().setAction(BROADCAST_SWAP_DRIVERS))
        }

        viewModel.error.observe(this) { message ->
            message?.let {
                toast(ErrorHandler.getErrorMessage(it, requireContext()))
            }
        }
    }
}